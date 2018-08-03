package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.InstanceType
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesResult

/**
 * Takes care of failed EC2 instance launches.
 * Currently, it mostly complains. We could hire a more patient nanny, which would actually retry the instance after
 * the initial complaints have achieved the desired effect.
 */
class InstanceNanny(
    private val ec2: ScrollingEc2,
    private val capacity: CapacityMediator
) {
    /**
     * Attempts to handle the [problem] with [launch]ing an instance.
     *
     * @return a fixed launch response
     */
    fun takeCare(
        problem: Exception,
        launch: RunInstancesRequest
    ): RunInstancesResult {
        val message = problem.message ?: "Empty message"
        if (message.contains("current instance limit") && message.contains("specified instance type")) {
            val type = InstanceType.fromValue(launch.instanceType)
            val bumpResponse = bumpCapacity(type)
            throw Exception("It's a capacity problem. $bumpResponse", problem)
        } else {
            throw Exception("Unable to understand the cause", problem)
        }
    }

    private fun bumpCapacity(
        type: InstanceType
    ): String = capacity.bump(
        limitType = "EC2 $type instance limit",
        desiredLimit = { maxOf(countAllocated(type) * 2, 5) }
    )

    private fun countAllocated(
        type: InstanceType
    ): Int = ec2
        .findInstances(
            ec2.allocated(),
            Filter("instance-type", listOf(type).map { it.toString() })
        )
        .size
}