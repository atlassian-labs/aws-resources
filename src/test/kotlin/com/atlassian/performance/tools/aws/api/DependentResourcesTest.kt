package com.atlassian.performance.tools.aws.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class DependentResourcesTest {

    @Test
    fun shouldBlockUntilBothAreReleasedWhenUserIsShorterThanDependency() {
        shouldBlockUntilBothAreReleased(
            userReleaseDuration = Duration.ofMillis(500),
            dependencyReleaseDuration = Duration.ofMillis(1000)
        )
    }

    @Test
    fun shouldBlockUntilBothAreReleasedWhenUserIsLongerThanDependency() {
        shouldBlockUntilBothAreReleased(
            userReleaseDuration = Duration.ofMillis(1000),
            dependencyReleaseDuration = Duration.ofMillis(500)
        )
    }

    private fun shouldBlockUntilBothAreReleased(
        userReleaseDuration: Duration,
        dependencyReleaseDuration: Duration
    ) {
        val userIsReleased = AtomicBoolean(false)
        val dependencyIsReleased = AtomicBoolean(false)
        val user = ReleasableResource(userReleaseDuration, userIsReleased)
        val dependency = ReleasableResource(dependencyReleaseDuration, dependencyIsReleased)
        val dependentResources = DependentResources(user, dependency)

        dependentResources.release().get()

        assertThat(userIsReleased.get() && dependencyIsReleased.get())
            .`as`("Both user and dependency should have been released by now")
            .isTrue()
    }

    private class ReleasableResource(
        private val releaseLength: Duration,
        private val completionFlag: AtomicBoolean
    ) : Resource {
        override fun isExpired() = true

        override fun release(): CompletableFuture<*> = CompletableFuture.runAsync {
            Thread.sleep(releaseLength.toMillis())
            completionFlag.set(true)
        }
    }
}