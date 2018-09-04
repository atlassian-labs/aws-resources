package com.atlassian.performance.tools.aws.api

import java.util.concurrent.CompletableFuture

data class DependentResources(
    private val user: Resource,
    private val dependency: Resource
) : Resource {
    override fun isExpired(): Boolean = user.isExpired()

    override fun release(): CompletableFuture<*> {
        return user.release().thenAccept { dependency.release() }
    }
}