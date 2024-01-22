package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.InstanceStateName.Terminated
import net.jcip.annotations.ThreadSafe
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

/**
 * Polls for instance state in batches to avoid throttling.
 *
 * Reuse instances of [TerminationPollingEc2] and maximize concurrency to get the most benefits of batching.
 */
@ThreadSafe
class TerminationPollingEc2(
    private val scrollingEc2: ScrollingEc2
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val polls: MutableMap<String, CompletableFuture<String>> = ConcurrentHashMap()

    init {
        timer(
            name = "ec2-polling",
            daemon = true,
            initialDelay = Duration.ofSeconds(5).toMillis(),
            period = Duration.ofSeconds(15).toMillis(),
            action = { tryPolling() }
        )
    }

    /**
     * @return future termination with the given [instanceId]
     */
    fun pollUntilTermination(
        instanceId: String
    ): CompletableFuture<String> {
        synchronized(polls) {
            return polls.computeIfAbsent(instanceId) { CompletableFuture() }
        }
    }

    private fun tryPolling() {
        try {
            synchronized(polls) {
                pollUntilTermination()
            }
        } catch (e: Exception) {
            logger.warn("Failed to poll instances", e)
        }
    }

    private fun pollUntilTermination() {
        if (polls.isEmpty()) {
            logger.debug("No instances to poll")
            return
        }
        val instanceIds = polls.keys.take(200)
        logger.debug("Polling $instanceIds")
        val foundInstanceIds = mutableSetOf<String>()
        scrollingEc2.scrollThroughInstances(
            Filter("instance-id", instanceIds)
        ) { instanceBatch ->
            instanceBatch
                .onEach { foundInstanceIds += it.instanceId }
                .filter { it.state.name == Terminated.toString() }
                .map { it.instanceId }
                .forEach { completeTermination(it) }
        }
        (instanceIds - foundInstanceIds).forEach { unseenInstanceId ->
            completeTermination(unseenInstanceId)
        }
    }

    private fun completeTermination(
        instanceId: String
    ) {
        logger.debug("$instanceId has terminated")
        polls
            .remove(instanceId)
            ?.complete(instanceId)
    }
}
