package com.atlassian.performance.tools.aws.api

import java.util.concurrent.CompletableFuture

class UnallocatedResource : Resource {

    override fun isExpired(): Boolean = false

    override fun release(): CompletableFuture<*> = CompletableFuture.completedFuture(Unit)
}