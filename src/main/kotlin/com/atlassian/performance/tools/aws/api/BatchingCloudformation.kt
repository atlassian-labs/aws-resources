package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.cloudformation.model.Stack
import com.atlassian.performance.tools.aws.InternalBatchingCloudformation
import net.jcip.annotations.ThreadSafe
import java.time.Duration
import java.util.concurrent.CompletableFuture

/**
 * Batches requests to AWS CloudFormation to avoid request throttling.
 *
 * Reuse instances of [BatchingCloudformation] and maximize concurrency to get the most benefits of batching.
 */
@ThreadSafe
@Deprecated(
    message = "Don't use BatchingCloudformation directly. Use a StackFormula instead."
)
class BatchingCloudformation(
    cloudformation: ScrollingCloudformation
) {
    private val internalBatching: InternalBatchingCloudformation = InternalBatchingCloudformation(
        cloudformation = cloudformation,
        refreshPeriod = Duration.ofMinutes(1)
    )

    fun findStack(
        stackName: String
    ): CompletableFuture<Stack?> = internalBatching.findStack(stackName)
}
