package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesResult
import com.amazonaws.services.cloudformation.model.ResourceStatus
import com.amazonaws.services.cloudformation.model.StackResource
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.InstanceState
import com.amazonaws.services.ec2.model.InstanceStateName
import com.amazonaws.services.ec2.model.InstanceStateName.*
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class StackNannyTest {

    @Test
    fun shouldComplainAboutGlobalInstanceLimit() {
        val capacity = MemorizingCapacityMediator()
        val nanny = StackNanny(
            cloudformation = PredefinedResourcesCloudformation(),
            ec2 = PredefinedFilteringEc2(),
            capacity = capacity
        )

        nanny.takeCare("a-failed-stack")

        assertThat(capacity.lastLimitType, equalTo("EC2 general instance limit"))
        assertThat(capacity.lastDesiredLimit, equalTo(20))
    }
}

/**
 * Describes predefined resources for a predefined stack.
 */
private class PredefinedResourcesCloudformation : FakeCloudformation() {

    override fun describeStackResources(
        describeStackResourcesRequest: DescribeStackResourcesRequest?
    ): DescribeStackResourcesResult {
        val requestedStackName = describeStackResourcesRequest!!.stackName
        return when (requestedStackName) {
            "a-failed-stack" -> describePredefinedResources()
            else -> throw Exception("No predefined resources for $requestedStackName")
        }
    }

    private fun describePredefinedResources(): DescribeStackResourcesResult {
        return DescribeStackResourcesResult().withStackResources(
            listOf(
                StackResource()
                    .withResourceType("AWS::EC2::SecurityGroup"),
                StackResource()
                    .withResourceType("AWS::EC2::Instance")
                    .withResourceStatus(ResourceStatus.CREATE_COMPLETE),
                StackResource()
                    .withResourceType("AWS::EC2::Instance")
                    .withResourceStatus(ResourceStatus.CREATE_FAILED)
                    .withResourceStatusReason("Instance failed to stabilize"),
                StackResource()
                    .withResourceType("AWS::EC2::Instance")
                    .withResourceStatus(ResourceStatus.CREATE_FAILED)
                    .withResourceStatusReason(
                        "Your quota allows for 0 more running instance(s). You requested at least 1"
                    )
            ).shuffled()
        )
    }
}

private class MemorizingCapacityMediator : CapacityMediator {

    var lastLimitType: String? = null
    var lastDesiredLimit: Int? = null

    override fun bump(
        limitType: String,
        desiredLimit: () -> Int
    ): String {
        lastLimitType = limitType
        lastDesiredLimit = desiredLimit()
        return "Bumped $limitType to ${desiredLimit()}"
    }
}

/**
 * EC2 service with predefined instances. Able to filter by [allocated].
 */
private class PredefinedFilteringEc2 : ScrollingEc2 {

    private val allocatedInstances = listOf(
        instance(Pending),
        instance(Running),
        instance(Running),
        instance(ShuttingDown),
        instance(Running),
        instance(Running),
        instance(Running),
        instance(Running),
        instance(Running),
        instance(Running)
    )

    private val allocatedFilter = Filter()

    override fun scrollThroughInstances(
        vararg filters: Filter,
        batchAction: (List<Instance>) -> Unit
    ) {
        throw Exception("I'm not ready to scroll")
    }

    override fun findInstances(vararg filters: Filter): List<Instance> {
        if (filters.single() == allocatedFilter) {
            return allocatedInstances
        } else {
            throw Exception("I'm not ready to handle other filters")
        }
    }

    override fun allocated(): Filter = allocatedFilter

    private fun instance(
        stateName: InstanceStateName
    ): Instance = Instance().withState(InstanceState().withName(stateName))
}