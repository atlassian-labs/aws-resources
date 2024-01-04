package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.ImageState
import com.amazonaws.services.ec2.model.InstanceStateName
import com.atlassian.performance.tools.aws.ami.AmiImage
import com.atlassian.performance.tools.aws.api.Resource
import com.atlassian.performance.tools.aws.api.TerminationBatchingEc2
import com.atlassian.performance.tools.aws.api.TerminationPollingEc2
import java.util.function.Consumer

internal class Ec2(
    private val ec2: AmazonEC2
) {
    fun consumeExpiredInstances(call: Consumer<List<Ec2Instance>>) {
        val scrollingEc2 = TokenScrollingEc2(ec2)
        val terminationPollingEc2 = TerminationPollingEc2(scrollingEc2)
        val terminationBatchingEc2 = TerminationBatchingEc2(ec2, terminationPollingEc2)

        val cleanInstanceStatuses = listOf(
            InstanceStateName.ShuttingDown,
            InstanceStateName.Terminated
        )
        scrollingEc2.scrollThroughInstances { instanceBatch ->
            val expiredInstances = instanceBatch
                .filter { InstanceStateName.fromValue(it.state.name) !in cleanInstanceStatuses }
                .map { Ec2Instance(it, terminationBatchingEc2) }
                .filter { it.isExpired() }
            call.accept(expiredInstances)
        }
    }

    fun listExpiredAmis(): List<Resource> {
        return ec2.describeImages()
            .images
            .filter { ImageState.fromValue(it.state) != ImageState.Deregistered }
            .map { AmiImage(image = it, ec2 = ec2) }
            .filter { it.isExpired() }
    }
}
