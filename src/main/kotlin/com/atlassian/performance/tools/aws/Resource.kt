package com.atlassian.performance.tools.aws

import java.util.concurrent.CompletableFuture

/**
 * We need to [release] unnecessary AWS resources to keep our bills sustainable.
 * Code, which allocates these resources should release them after the resources are no longer necessary.
 * However, every process can fail and might not reach its release phase. That's why a secondary, external cleanup
 * process is necessary. The external processes are allowed to [release] the resource if it [isExpired].
 */
interface Resource {

    fun isExpired(): Boolean

    fun release(): CompletableFuture<*>
}