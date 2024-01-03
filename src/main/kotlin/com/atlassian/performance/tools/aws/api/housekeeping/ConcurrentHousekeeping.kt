package com.atlassian.performance.tools.aws.api.housekeeping

import com.atlassian.performance.tools.aws.Cloudformation
import com.atlassian.performance.tools.aws.Ec2
import com.atlassian.performance.tools.aws.api.*
import com.atlassian.performance.tools.concurrency.api.finishBy
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.Instant.now
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class ConcurrentHousekeeping(
    private val stackTimeout: Duration,
    private val instanceTimeout: Duration,
    private val amiTimeout: Duration
) : Housekeeping {
    private val logger = LogManager.getLogger(this::class.java)

    override fun cleanLeftovers(aws: Aws) {
        Ec2(aws.ec2).consumeExpiredInstances(Consumer { instances ->
            waitUntilReleased(instances, instanceTimeout)
        })

        val amis = Ec2(aws.ec2).listExpiredAmis()
        waitUntilReleased(amis, amiTimeout)

        val keys = aws.ec2.describeKeyPairs().keyPairs.map { key ->
            RemoteSshKey(SshKeyName(key.keyName), aws.ec2)
        }.filter { it.isExpired() }

        val securityGroups = aws.ec2.describeSecurityGroups().securityGroups.map { securityGroup ->
            Ec2SecurityGroup(securityGroup, aws.ec2)
        }.filter { it.isExpired() }

        waitUntilReleased(keys)
        waitUntilReleased(securityGroups)

        Cloudformation(aws, aws.cloudformation).consumeExpiredStacks(Consumer { stacks ->
            waitUntilReleased(stacks, stackTimeout)
        })
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

    class Builder {
        private var stackTimeout: Duration = Duration.ofMinutes(5)
        private var instanceTimeout: Duration = Duration.ofMinutes(4)
        private var amiTimeout: Duration = Duration.ofMinutes(2)

        fun stackTimeout(stackTimeout: Duration) = apply { this.stackTimeout = stackTimeout }
        fun instanceTimeout(instanceTimeout: Duration) = apply { this.instanceTimeout = instanceTimeout }
        fun amiTimeout(amiTimeout: Duration) = apply { this.amiTimeout = amiTimeout }

        fun build(): Housekeeping {
            return ConcurrentHousekeeping(
                stackTimeout = stackTimeout,
                instanceTimeout = instanceTimeout,
                amiTimeout = amiTimeout
            )
        }
    }
}
