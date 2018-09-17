package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.cloudformation.model.Stack
import net.jcip.annotations.ThreadSafe
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

/**
 * Batches requests to AWS CloudFormation to avoid request throttling.
 *
 * Reuse instances of [BatchingCloudformation] and maximize concurrency to get the most benefits of batching.
 *
 * @param [refreshPeriod] Gives time for requests to accumulate before sending them as a batch.
 */
@ThreadSafe
class BatchingCloudformation internal constructor(
    private val cloudformation: ScrollingCloudformation,
    refreshPeriod: Duration
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val namesToStackRequests: MutableMap<String, CompletableFuture<Stack?>> = ConcurrentHashMap()

    private val timer: Lazy<Timer> = lazy {
        timer(
            name = "batching-cloudformation-refresh",
            daemon = true,
            period = refreshPeriod.toMillis(),
            action = { tryToRefresh() }
        )
    }

    @Deprecated(
        message = "The constructor wasn't supposed to become API. Use the instance exposed by Aws instead.",
        replaceWith = ReplaceWith("aws.batchingCloudformation")
    )
    constructor(
        cloudformation: ScrollingCloudformation
    ) : this(
        cloudformation = cloudformation,
        refreshPeriod = Duration.ofMinutes(1)
    )

    fun findStack(
        stackName: String
    ): CompletableFuture<Stack?> {
        synchronized(namesToStackRequests) {
            val stackRequest = namesToStackRequests.getOrPut(stackName) { CompletableFuture() }
            timer.value
            return stackRequest
        }
    }

    private fun tryToRefresh() {
        try {
            synchronized(namesToStackRequests) {
                refresh()
            }
        } catch (e: Exception) {
            logger.warn("Failed to refresh stack statuses", e)
        }
    }

    private fun refresh() {
        if (namesToStackRequests.isEmpty()) {
            logger.trace("Nothing to request. Omitting.")
            return
        }
        logger.debug("Starting refresh. Looking for: ${namesToStackRequests.keys}")
        val foundStackNames = mutableListOf<String>()
        cloudformation.scrollThroughStacks { stacks ->
            stacks.forEach {
                val request = namesToStackRequests.remove(it.stackName)
                if (request != null) {
                    request.complete(it)
                    foundStackNames += it.stackName
                }
            }
        }
        logger.debug("Finished refresh. Found: $foundStackNames")
        namesToStackRequests.forEach { it.value.complete(null) }
        namesToStackRequests.clear()
    }
}