package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest
import com.amazonaws.services.cloudformation.model.ResourceStatus
import com.amazonaws.services.cloudformation.model.ResourceStatus.CREATE_FAILED

/**
 * Takes care of CloudFormation stacks, tries to heal them, helps them grow.
 * Currently, it mostly complains. We could hire a more patient nanny, which would actually retry the stack after
 * the initial complaints have achieved the desired effect.
 */
class StackNanny(
    private val cloudformation: AmazonCloudFormation,
    private val ec2: ScrollingEc2,
    private val capacity: CapacityMediator
) {
    /**
     * Complains about quotas to the support if necessary.
     */
    fun takeCare(
        stackName: String
    ) {
        val unluckyInstances = cloudformation
            .describeStackResources(DescribeStackResourcesRequest().withStackName(stackName))
            .stackResources
            .filter { it.resourceType == "AWS::EC2::Instance" }
            .filter { ResourceStatus.fromValue(it.resourceStatus) == CREATE_FAILED }
        if (unluckyInstances.any { it.resourceStatusReason.startsWith("Your quota allows") }) {
            throw Exception("$stackName stack failed due to a capacity problem: ${bumpGlobalLimit()}")
        }
    }

    private fun bumpGlobalLimit(): String = capacity.bump(
        limitType = "EC2 general instance limit",
        desiredLimit = { countAllocatedInstances() * 2 }
    )

    private fun countAllocatedInstances(): Int = ec2.findInstances(ec2.allocated()).size
}