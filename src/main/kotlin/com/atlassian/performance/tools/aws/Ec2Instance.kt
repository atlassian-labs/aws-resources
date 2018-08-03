package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.model.Instance
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.CompletableFuture

class Ec2Instance(
    private val instance: Instance,
    private val terminationBatchingEc2: TerminationBatchingEc2
) : Resource {

    private val expiry: Instant? = Investment.parseExpiry(instance.tags.map { Tag(it) })

    override fun isExpired(): Boolean = expiry?.isBefore(now()) ?: false

    override fun release(): CompletableFuture<*> {
        return terminationBatchingEc2.terminate(instance.instanceId)
    }
}