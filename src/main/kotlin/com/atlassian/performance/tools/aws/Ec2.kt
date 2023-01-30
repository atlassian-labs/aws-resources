package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.ImageState
import com.amazonaws.services.ec2.model.InstanceStateName
import com.atlassian.performance.tools.aws.ami.AmiImage
import com.atlassian.performance.tools.aws.api.Resource
import com.atlassian.performance.tools.aws.api.TerminationBatchingEc2
import com.atlassian.performance.tools.aws.api.TerminationPollingEc2

internal class Ec2(
    private val ec2: AmazonEC2
) {
    fun listExpiredInstances(): List<Resource> {
        val scrollingEc2 = TokenScrollingEc2(ec2)
        val terminationPollingEc2 = TerminationPollingEc2(scrollingEc2)
        val terminationBatchingEc2 = TerminationBatchingEc2(ec2, terminationPollingEc2)

        val instances = mutableListOf<Resource>()
        val cleanInstanceStatuses = listOf(
            InstanceStateName.ShuttingDown,
            InstanceStateName.Terminated
        )
        scrollingEc2.scrollThroughInstances { instanceBatch ->
            instanceBatch
                .filter { InstanceStateName.fromValue(it.state.name) !in cleanInstanceStatuses }
                .forEach { instances += Ec2Instance(it, terminationBatchingEc2) }
        }
        return instances.filter { it.isExpired() }
    }

    fun listExpiredAmis(): List<Resource> {
        return ec2.describeImages()
            .images
            .filter { ImageState.fromValue(it.state) != ImageState.Deregistered }
            .map { AmiImage(image = it, ec2 = ec2) }
            .filter { it.isExpired() }
    }

}
