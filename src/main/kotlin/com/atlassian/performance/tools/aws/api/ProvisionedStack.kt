package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.cloudformation.model.*
import com.amazonaws.services.ec2.model.*
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription
import com.amazonaws.services.identitymanagement.model.DeleteRolePolicyRequest
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyRequest
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesRequest
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesRequest
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.MultiObjectDeleteException
import com.atlassian.performance.tools.aws.api.Investment.TagKeys.bambooBuildKey
import com.atlassian.performance.tools.aws.api.Investment.TagKeys.lifespanKey
import com.atlassian.performance.tools.aws.api.Investment.TagKeys.userKey
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.CompletableFuture

/**
 * A provisioned CloudFormation stack.
 */
class ProvisionedStack(
    stack: Stack,
    private val aws: Aws
) : Resource {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    val stackName: String = stack.stackName
    val status: String = stack.stackStatus
    private val tags = stack.tags

    /**
     * Minimum amount of time before the stack is considered expired.
     *
     * Prevents the stack from being cleaned up too early by an external process.
     */
    private val lifespan: Duration = Investment.parseLifespan(tags.map { Tag(it) })
        ?: throw Exception("The stack '$stack' is not provisioned. It misses $lifespanKey tag.")
    val expiry: Instant = stack.creationTime.toInstant() + lifespan

    val user: String? = tags
        .filter { it.key == userKey }
        .map { it.value }
        .firstOrNull()

    val bambooBuild: String? = tags
        .filter { it.key == bambooBuildKey }
        .map { it.value }
        .firstOrNull()

    fun listMachines(): List<Instance> {
        return aws
            .ec2
            .describeInstances(DescribeInstancesRequest().withInstanceIds(
                filterResources("AWS::EC2::Instance").map { it.physicalResourceId }
            ))
            .reservations
            .flatMap { it.instances }
    }

    fun findStorage(
        logicalBucketName: String,
        prefix: String
    ) = Storage(
        s3 = aws.s3,
        prefix = prefix,
        bucketName = findLogicalResource("AWS::S3::Bucket", logicalBucketName)
    )

    private fun filterResources(
        resourceType: String
    ): List<StackResourceSummary> {
        return aws
            .cloudformation
            .listStackResources(
                ListStackResourcesRequest().withStackName(stackName)
            )
            .stackResourceSummaries
            .filter { it.resourceType == resourceType }
            .filter { it.isAlive() }
    }

    private fun StackResourceSummary.isAlive(): Boolean {
        return ResourceStatus.fromValue(resourceStatus) !in listOf(
            ResourceStatus.DELETE_IN_PROGRESS,
            ResourceStatus.DELETE_COMPLETE
        )
    }

    private fun findLogicalResource(
        resourceType: String,
        logicalId: String
    ): String {
        val resource = filterResources(resourceType)
            .find { it.logicalResourceId == logicalId }
            ?.physicalResourceId
        return resource ?: throw RuntimeException("$stackName does not have $resourceType with '$logicalId' logical id")
    }

    fun findInstanceProfile(
        logicalId: String
    ): String = findLogicalResource("AWS::IAM::InstanceProfile", logicalId)

    fun findLoadBalancer(): LoadBalancerDescription {
        val loadBalancerName = filterResources("AWS::ElasticLoadBalancing::LoadBalancer")
            .single()
            .physicalResourceId

        return aws
            .loadBalancer
            .describeLoadBalancers()
            .loadBalancerDescriptions
            .single { it.loadBalancerName == loadBalancerName }
    }

    fun findSubnet(
        logicalId: String
    ): Subnet = aws
        .ec2
        .describeSubnets(
            DescribeSubnetsRequest().withSubnetIds(
                findLogicalResource(resourceType = "AWS::EC2::Subnet", logicalId = logicalId)
            )
        )
        .subnets
        .single()

    fun findVpc(
        logicalId: String
    ): Vpc = aws
        .ec2
        .describeVpcs(
            DescribeVpcsRequest().withVpcIds(
                findLogicalResource(resourceType = "AWS::EC2::VPC", logicalId = logicalId)
            )
        )
        .vpcs
        .single()

    override fun isExpired(): Boolean {
        return expiry < now()
    }

    override fun release(): CompletableFuture<*> {
        return CompletableFuture.runAsync {
            cleanBuckets()
            cleanRoles()
            logger.debug("Deleting stack $stackName")
            aws.cloudformation.deleteStack(DeleteStackRequest().withStackName(stackName))
        }
    }

    private fun cleanBuckets() {
        logger.debug("Cleaning buckets in $stackName")
        filterResources("AWS::S3::Bucket")
            .map { it.physicalResourceId }
            .forEach { cleanBucket(it) }
    }

    private fun cleanBucket(
        bucketName: String
    ) {
        logger.debug("Cleaning bucket $bucketName")
        do {
            val objectKeys = aws
                .s3
                .listObjects(bucketName)
                .objectSummaries
                .map { it.key }
            deleteObjects(bucketName, objectKeys)
        } while (objectKeys.isNotEmpty())
    }

    private fun deleteObjects(
        bucketName: String,
        objectKeys: List<String>
    ) {
        if (objectKeys.isEmpty()) {
            return
        }
        try {
            logger.debug("Deleting ${objectKeys.size} objects from $bucketName")
            val versionedKeys = objectKeys.map { DeleteObjectsRequest.KeyVersion(it) }
            aws.s3.deleteObjects(
                DeleteObjectsRequest(bucketName)
                    .withKeys(versionedKeys)
                    .withQuiet(true)
            )
        } catch (e: MultiObjectDeleteException) {
            e.errors.map { it.key }.forEach {
                if (aws.s3.doesObjectExist(bucketName, it)) {
                    throw RuntimeException("Failed to delete $it from $bucketName", e)
                } else {
                    logger.debug("Lost a race to delete $it from $bucketName")
                }
            }
        }
    }

    private fun cleanRoles() {
        filterResources("AWS::IAM::Role")
            .map { it.physicalResourceId }
            .forEach { cleanRole(it) }
    }

    private fun cleanRole(
        roleName: String
    ) {
        aws
            .iam
            .listAttachedRolePolicies(
                ListAttachedRolePoliciesRequest().withRoleName(roleName)
            )
            .attachedPolicies
            .map { it.policyArn }
            .forEach { detachAttachedPolicy(it, roleName) }
        aws
            .iam
            .listRolePolicies(
                ListRolePoliciesRequest().withRoleName(roleName)
            )
            .policyNames
            .forEach { deleteInlinePolicy(it, roleName) }
    }

    private fun detachAttachedPolicy(
        policyArn: String,
        roleName: String
    ) {
        aws.iam.detachRolePolicy(
            DetachRolePolicyRequest()
                .withRoleName(roleName)
                .withPolicyArn(policyArn)
        )
    }

    private fun deleteInlinePolicy(
        policyName: String,
        roleName: String
    ) {
        aws.iam.deleteRolePolicy(
            DeleteRolePolicyRequest()
                .withPolicyName(policyName)
                .withRoleName(roleName)
        )
    }

    override fun toString() = "ProvisionedStack(stackName=$stackName, lifespan=$lifespan)"
}
