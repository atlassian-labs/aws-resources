package com.atlassian.performance.tools.aws

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProvider
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
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.support.AWSSupport
import com.amazonaws.services.support.AWSSupportClientBuilder
import com.atlassian.performance.tools.concurrency.finishBy
import com.atlassian.performance.tools.io.readResourceText
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.time.Instant.now
import java.util.concurrent.CompletableFuture

class Aws(
    val region: Regions,
    credentialsProvider: AWSCredentialsProvider
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
    private val support: AWSSupport = AWSSupportClientBuilder.standard()
        .withRegion(Regions.US_EAST_1)
        .withCredentials(credentialsProvider)
        .build()
    private val scrollingCloudformation = ScrollingCloudformation(cloudformation)
    val batchingCloudformation by lazy { BatchingCloudformation(scrollingCloudformation) }
    private val scrollingEc2: ScrollingEc2 = TokenScrollingEc2(ec2)
    private val terminationPollingEc2 by lazy { TerminationPollingEc2(scrollingEc2) }
    val terminationBatchingEc2 by lazy { TerminationBatchingEc2(ec2, terminationPollingEc2) }
    val loadBalancer: AmazonElasticLoadBalancing = AmazonElasticLoadBalancingClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .build()

    private val capacity = SupportCapacityMediator(support, region)
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

    val defaultAmi: String by lazy {
        ec2
            .describeImages(
                DescribeImagesRequest().withFilters(
                    Filter("name", listOf("ubuntu/images/hvm-ssd/ubuntu-xenial-16.04-amd64-server-20171011"))
                )
            )
            .images
            .map { it.imageId }
            .sorted()
            .first()
    }
    val awaitingEc2: AwaitingEc2 by lazy { AwaitingEc2(ec2, terminationBatchingEc2, instanceNanny, defaultAmi) }

    val availabilityZones: List<AvailabilityZone> by lazy {
        ec2
            .describeAvailabilityZones()
            .availabilityZones
            .filter { AvailabilityZoneState.fromValue(it.state) == AvailabilityZoneState.Available }
    }

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

    fun cleanLeftovers() {
        val stacks = Cloudformation(this, cloudformation).listExpiredStacks()
        waitUntilReleased(stacks)

        val instances = Ec2(ec2).listExpiredInstances()
        waitUntilReleased(instances, timeout = Duration.ofMinutes(2))

        val keys = ec2.describeKeyPairs().keyPairs.map { key ->
            RemoteSshKey(SshKeyName(key.keyName), ec2)
        }
        val securityGroups = ec2.describeSecurityGroups().securityGroups.map { securityGroup ->
            Ec2SecurityGroup(securityGroup, ec2)
        }
        waitUntilReleased(keys)
        waitUntilReleased(securityGroups)
    }

    private fun waitUntilReleased(
        resources: List<Resource>,
        timeout: Duration = Duration.ofSeconds(15)
    ) {
        val deadline = now() + timeout
        resources
            .mapNotNull { startReleasingIfExpired(it) }
            .forEach { it.finishBy(deadline, logger) }
    }

    private fun startReleasingIfExpired(
        resource: Resource
    ): CompletableFuture<*>? {
        return if (resource.isExpired()) {
            resource.release().handle { throwable, _ ->
                if (throwable != null) {
                    logger.error("$resource failed to release itself", throwable)
                }
            }
        } else {
            null
        }
    }

    fun listDisposableStacks(): List<Stack> {
        return Cloudformation(this, cloudformation).listDisposableStacks()
    }
}