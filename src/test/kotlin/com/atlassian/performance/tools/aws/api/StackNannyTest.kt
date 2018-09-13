package com.atlassian.performance.tools.aws.api

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesResult
import com.amazonaws.services.cloudformation.model.ResourceStatus
import com.amazonaws.services.cloudformation.model.StackResource
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.InstanceState
import com.amazonaws.services.ec2.model.InstanceStateName
import com.amazonaws.services.ec2.model.InstanceStateName.*
import com.atlassian.performance.tools.aws.FakeCloudformation
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class StackNannyTest {

    @Test
    fun shouldComplainAboutGlobalInstanceLimit() {
        val nanny = StackNanny(
            cloudformation = PredefinedResourcesCloudformation(),
            ec2 = PredefinedFilteringEc2(),
            capacity = TextCapacityMediator(Regions.US_EAST_1)
        )

        var exception: Exception? = null
        try {
            nanny.takeCare("a-failed-stack")
        } catch (e: Exception) {
            exception = e
        }

        assertThat(
            exception?.message,
            equalTo("a-failed-stack stack failed due to a capacity problem: You either need to bump EC2 general instance limit to 20 in us-east-1 manually or inject SupportCapacityMediator into Aws for automatic management")
        )
    }
}

/**
 * Describes predefined resources for a predefined stack.
 */
private class PredefinedResourcesCloudformation : AmazonCloudFormation by FakeCloudformation() {

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