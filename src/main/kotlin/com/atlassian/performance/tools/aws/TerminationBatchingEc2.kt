package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import net.jcip.annotations.ThreadSafe
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

/**
 * Batches termination requests to AWS EC2 to avoid request throttling.
 * Reuse instances of [TerminationBatchingEc2] and maximize concurrency to get the most benefits of batching.
 */
@ThreadSafe
class TerminationBatchingEc2(
    private val ec2: AmazonEC2,
    private val polling: TerminationPollingEc2
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val terminations: MutableMap<String, CompletableFuture<String>> = ConcurrentHashMap()

    init {
        timer(
            name = "ec2-termination",
            daemon = true,
            initialDelay = Duration.ofSeconds(5).toMillis(),
            period = Duration.ofSeconds(30).toMillis(),
            action = { tryToTerminate() }
        )
    }

    fun terminate(
        instanceId: String
    ): CompletableFuture<String> {
        synchronized(terminations) {
            return terminations.computeIfAbsent(instanceId) { CompletableFuture() }
        }
    }

    private fun tryToTerminate() {
        try {
            synchronized(terminations) {
                terminate()
            }
        } catch (e: Exception) {
            logger.warn("Failed to terminate ${terminations.size} instances", e)
        }
    }

    private fun terminate() {
        if (terminations.isEmpty()) {
            logger.trace("No instances to terminate")
            return
        }
        val instanceIds = terminations.keys
        logger.debug("Starting batch termination of $instanceIds")
        ec2.terminateInstances(TerminateInstancesRequest().withInstanceIds(instanceIds))
        instanceIds.forEach { instanceId ->
            polling
                .pollUntilTermination(instanceId)
                .thenAccept { terminatedId -> terminations.remove(terminatedId)?.complete(terminatedId) }
        }
    }
}