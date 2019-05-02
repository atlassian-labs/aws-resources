package com.atlassian.performance.tools.aws.api

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.retry.PredefinedBackoffStrategies
import com.amazonaws.retry.PredefinedRetryPolicies
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.AvailabilityZone
import com.amazonaws.services.ec2.model.AvailabilityZoneState
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder
import com.amazonaws.services.rds.AmazonRDS
import com.amazonaws.services.rds.AmazonRDSClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.atlassian.performance.tools.aws.Cloudformation
import com.atlassian.performance.tools.aws.Ec2
import com.atlassian.performance.tools.aws.InternalBatchingCloudformation
import com.atlassian.performance.tools.aws.TokenScrollingEc2
import com.atlassian.performance.tools.concurrency.api.finishBy
import com.atlassian.performance.tools.io.api.readResourceText
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.time.Instant.now
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate

class Aws private constructor(
    val region: Regions,
    credentialsProvider: AWSCredentialsProvider,
    capacity: CapacityMediator,
    batchingCloudformationRefreshPeriod: Duration,
    regionsWithHousekeeping: List<Regions>,
    requireHousekeeping: Boolean,
    availabilityZoneFilter: (AvailabilityZone) -> Boolean
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    val ec2: AmazonEC2 = AmazonEC2ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .build()
    val s3: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .build()
    val rds: AmazonRDS = AmazonRDSClientBuilder.standard()
         .withRegion(region)
        .withCredentials(credentialsProvider)
        .build()
    val cloudformation: AmazonCloudFormation = AmazonCloudFormationClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .withClientConfiguration(
            ClientConfiguration().withRetryPolicy(
                RetryPolicy(
                    PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION,
                    PredefinedBackoffStrategies.EqualJitterBackoffStrategy(
                        Duration.ofSeconds(2).toMillis().toInt(),
                        Duration.ofMinutes(3).toMillis().toInt()
                    ),
                    6,
                    false
                )
            )
        )
        .build()
    val iam: AmazonIdentityManagement = AmazonIdentityManagementClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .build()
    private val scrollingCloudformation = ScrollingCloudformation(cloudformation)
    internal val batchingCfn = InternalBatchingCloudformation(scrollingCloudformation, batchingCloudformationRefreshPeriod)
    @Deprecated(
        message = "Don't use batchingCloudformation directly. Use a StackFormula instead."
    )
    val batchingCloudformation = BatchingCloudformation(scrollingCloudformation)
    private val scrollingEc2: ScrollingEc2 = TokenScrollingEc2(ec2)
    private val terminationPollingEc2 by lazy { TerminationPollingEc2(scrollingEc2) }
    val terminationBatchingEc2 by lazy { TerminationBatchingEc2(ec2, terminationPollingEc2) }
    val loadBalancer: AmazonElasticLoadBalancing = AmazonElasticLoadBalancingClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .build()

    val stackNanny = StackNanny(cloudformation, scrollingEc2, capacity)
    private val instanceNanny = InstanceNanny(scrollingEc2, capacity)

    private val shortTermStorage: ProvisionedStack by lazy {
        StackFormula(
            investment = Investment(
                useCase = "Transport files necessary to run the tests",
                reuseKey = { "jpt-short-term-storage" },
                lifespan = Duration.ofDays(30),
                disposable = false
            ),
            cloudformationTemplate = readResourceText("aws/short-term-storage.yaml"),
            aws = this
        ).provision()
    }

    private val customDatasetStorage: ProvisionedStack by lazy {
        StackFormula(
            investment = Investment(
                useCase = "Store custom datasets",
                reuseKey = { "jpt-custom-datasets-storage" },
                lifespan = Duration.ofDays(800),
                disposable = false
            ),
            cloudformationTemplate = readResourceText("aws/custom-datasets-storage.yaml"),
            aws = this
        ).provision()
    }

    private val amiName = "ubuntu/images/hvm-ssd/ubuntu-xenial-16.04-amd64-server-20180912"
    val defaultAmi: String by lazy {
        ec2
            .describeImages(
                DescribeImagesRequest().withFilters(
                    Filter("name", listOf(amiName))
                )
            )
            .images
            .map { it.imageId }
            .sorted()
            .firstOrNull()
            ?: throw Exception("Failed to find image $amiName in $region")

    }
    val awaitingEc2: AwaitingEc2 by lazy { AwaitingEc2(ec2, terminationBatchingEc2, instanceNanny, defaultAmi) }

    val availabilityZones: List<AvailabilityZone> by lazy {
        ec2
            .describeAvailabilityZones()
            .availabilityZones
            .filter { AvailabilityZoneState.fromValue(it.state) == AvailabilityZoneState.Available }
            .filter(availabilityZoneFilter)
    }

    init {
        if (region !in regionsWithHousekeeping) {
            val message = """
                *************************************************************************************************

                ATTENTION!
                YOU RISK LOSING MONEY on unnecessary AWS charges if you don't clean up AWS resources in
                the ${region.describe()} AWS region.

                All AWS resources provisioned by this code are marked with a `lifespan` or `expiry` tag/metadata.
                You can control that duration via the `Investment` class.
                After a resource is past its lifespan/expiry, it is eligible for cleanup by any housekeeping call.
                You can invoke housekeeping via the `cleanLeftovers` method on this object.
                Make sure the `region` set in the constructor equals $region.
                Run this housekeeping periodically, for example every 30 minutes. A CI server or cron can work.

                When you do ensure that this region has housekeeping taken care of, you can get rid of this error,
                by adding the region to the `regionsWithHousekeeping` list in the constructor.

                **************************************************************************************************
            """.trimIndent()
            if (requireHousekeeping) {
                throw Exception(message)
            } else {
                logger.error(message)
            }
        }
        val safeRegionsList = regionsWithHousekeeping.joinToString(prefix = "- ", separator = "\n")
        logger.info(
            """
                You already declared that AWS housekeeping is taken care of in the following regions:
                $safeRegionsList
                Make sure this list makes sense from time to time.
            """.trimIndent()
        )
    }

    /**
     * @param [credentialsProvider] A way to authenticate with your AWS account. This account will be used and billed.
     * @param [region] AWS region to allocate all resources in. Note that many kinds of resources are region-specific.
     * @param [regionsWithHousekeeping] Declaration, that the caller took care of AWS housekeeping in these regions.
     * @param [capacity] A way to manage capacity if you use more AWS resources than usual.
     * @param [batchingCloudformationRefreshPeriod] Gives time for the batch requests to accumulate single requests.
     *                                              Trades off between AWS throttling and Cloudformation latency.
     */
    @Deprecated(
        message = "Use Builder instead.",
        replaceWith = ReplaceWith(
            expression = "Aws.Builder(region = region)" +
                ".credentialsProvider(credentialsProvider)" +
                ".regionsWithHousekeeping(regionsWithHousekeeping)" +
                ".capacity(capacity)" +
                ".batchingCloudformationRefreshPeriod(batchingCloudformationRefreshPeriod)" +
                ".build()"
        )
    )
    constructor(
        credentialsProvider: AWSCredentialsProvider,
        region: Regions,
        regionsWithHousekeeping: List<Regions>,
        capacity: CapacityMediator,
        batchingCloudformationRefreshPeriod: Duration
    ) : this(
        region = region,
        credentialsProvider = credentialsProvider,
        capacity = capacity,
        batchingCloudformationRefreshPeriod = batchingCloudformationRefreshPeriod,
        regionsWithHousekeeping = regionsWithHousekeeping,
        requireHousekeeping = true,
        availabilityZoneFilter = { true }
    )

    @Deprecated(
        message = "Use Builder instead. " +
            "This constructor is unsafe, because it doesn't fail-fast for missing AWS housekeeping declarations. " +
            "It's left here only for compatibility. Move away from it as fast as possible.",
        replaceWith = ReplaceWith(
            expression = "Aws.Builder(region = region)" +
                ".credentialsProvider(credentialsProvider)" +
                ".capacity(capacity)" +
                ".batchingCloudformationRefreshPeriod(batchingCloudformationRefreshPeriod)" +
                ".build()"
        )
    )
    @JvmOverloads
    constructor(
        region: Regions,
        credentialsProvider: AWSCredentialsProvider,
        capacity: CapacityMediator = TextCapacityMediator(region),
        batchingCloudformationRefreshPeriod: Duration = Duration.ofMinutes(1)
    ) : this(
        region = region,
        credentialsProvider = credentialsProvider,
        capacity = capacity,
        batchingCloudformationRefreshPeriod = batchingCloudformationRefreshPeriod,
        regionsWithHousekeeping = emptyList(),
        requireHousekeeping = false,
        availabilityZoneFilter = { true }
    )

    fun jiraStorage(
        nonce: String
    ) = shortTermStorage.findStorage("JiraBucket", nonce)

    fun virtualUsersStorage(
        nonce: String
    ) = shortTermStorage.findStorage("VirtualUsersBucket", nonce)

    fun resultsStorage(
        nonce: String
    ) = shortTermStorage.findStorage("ResultsBucket", nonce)

    fun shortTermStorageAccess() = shortTermStorage.findInstanceProfile("AccessProfile")

    fun customDatasetStorage(
        datasetName: String
    ) = customDatasetStorage.findStorage("DatasetBucket", datasetName)

    /**
     * Releases all the expired AWS resources allocated by JPT.
     */
    fun cleanLeftovers() {
        cleanLeftovers(
            stacksReleaseTimeout = Duration.ofMinutes(5),
            ec2ReleaseTimeout = Duration.ofMinutes(2)
        )
    }

    /**
     * Releases all the expired AWS resources allocated by JPT.
     *
     * @param stacksReleaseTimeout timeout for releasing expired cloudformation stacks.
     * @param ec2ReleaseTimeout timeout for releasing expired ec2 instances.
     *
     * @since 1.5.0
     */
    fun cleanLeftovers(
        stacksReleaseTimeout : Duration,
        ec2ReleaseTimeout: Duration
    ) {
        val stacks = Cloudformation(this, cloudformation).listExpiredStacks()
        waitUntilReleased(stacks, stacksReleaseTimeout)

        val instances = Ec2(ec2).listExpiredInstances()
        waitUntilReleased(instances, ec2ReleaseTimeout)

        val keys = ec2.describeKeyPairs().keyPairs.map { key ->
            RemoteSshKey(SshKeyName(key.keyName), ec2)
        }.filter { it.isExpired() }

        val securityGroups = ec2.describeSecurityGroups().securityGroups.map { securityGroup ->
            Ec2SecurityGroup(securityGroup, ec2)
        }.filter { it.isExpired() }

        waitUntilReleased(keys)
        waitUntilReleased(securityGroups)
    }

    private fun waitUntilReleased(
        resources: List<Resource>,
        timeout: Duration = Duration.ofSeconds(15)
    ) {
        val deadline = now() + timeout
        resources
            .map { startReleasing(it) }
            .forEach { it.finishBy(deadline, logger) }
    }

    private fun startReleasing(
        resource: Resource
    ): CompletableFuture<*> {
        if (!resource.isExpired()) {
            throw Exception("You can't release $resource. It hasn't expired.")
        }
        return resource.release().handle { throwable, _ ->
            if (throwable != null) {
                logger.error("$resource failed to release itself", throwable)
            }
        }
    }

    fun listDisposableStacks(): List<Stack> {
        return Cloudformation(this, cloudformation).listDisposableStacks()
    }

    private fun Regions.describe(): String = "$name ($description)"

    /**
     * @param [region] AWS region to allocate all resources in. Note that many kinds of resources are region-specific.
     */
    class Builder constructor(
        private val region: Regions
    ) {
        private var credentialsProvider: AWSCredentialsProvider = DefaultAWSCredentialsProviderChain()
        private var regionsWithHousekeeping: List<Regions> = emptyList()
        private var capacity: CapacityMediator = TextCapacityMediator(region)
        private var batchingCloudformationRefreshPeriod: Duration = Duration.ofMinutes(1)
        private var availabilityZoneFilter: Predicate<AvailabilityZone> = Predicate { true }

        /**
         * @param [credentialsProvider] A way to authenticate with your AWS account. This account will be used and billed.
         */
        fun credentialsProvider(credentialsProvider: AWSCredentialsProvider): Builder =
            apply { this.credentialsProvider = credentialsProvider }
        /**
         * @param [regionsWithHousekeeping] Declaration, that the caller took care of AWS housekeeping in these regions.
         */
        fun regionsWithHousekeeping(regionsWithHousekeeping: List<Regions>): Builder =
            apply { this.regionsWithHousekeeping = regionsWithHousekeeping }

        /**
         * @param [capacity] A way to manage capacity if you use more AWS resources than usual.
         */
        fun capacity(capacity: CapacityMediator): Builder = apply { this.capacity = capacity }

        /**
         * @param [batchingCloudformationRefreshPeriod] Gives time for the batch requests to accumulate single requests.
         *                                              Trades off between AWS throttling and Cloudformation latency.
         */
        fun batchingCloudformationRefreshPeriod(batchingCloudformationRefreshPeriod: Duration): Builder =
            apply { this.batchingCloudformationRefreshPeriod = batchingCloudformationRefreshPeriod }

        /**
         * @param [availabilityZoneFilter] A way to choose specific AWS availability zones.
         */
        fun availabilityZoneFilter(availabilityZoneFilter: Predicate<AvailabilityZone>): Builder =
            apply { this.availabilityZoneFilter = availabilityZoneFilter }

        fun build(): Aws = Aws(
            region = region,
            credentialsProvider = credentialsProvider,
            capacity = capacity,
            batchingCloudformationRefreshPeriod = batchingCloudformationRefreshPeriod,
            regionsWithHousekeeping = regionsWithHousekeeping,
            requireHousekeeping = true,
            availabilityZoneFilter = { availabilityZoneFilter.test(it) }
        )
    }
}