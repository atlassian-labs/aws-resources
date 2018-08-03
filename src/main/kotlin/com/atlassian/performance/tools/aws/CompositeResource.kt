package com.atlassian.performance.tools.aws

import java.util.concurrent.CompletableFuture

data class CompositeResource(
    private val resources: List<Resource>
) : Resource {
    override fun isExpired(): Boolean = resources.any { it.isExpired() }

    override fun release(): CompletableFuture<*> {
        val releases = resources.map { it.release() }
        return CompletableFuture.allOf(*releases.toTypedArray())
    }
}