package com.atlassian.performance.tools.aws

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CountingPredicateTest {
    @Test
    fun countingStartsFrom1() {
        val indices = mutableListOf<Int>()
        val predicate = CountingPredicate<Unit> { index, _ -> indices.add(index); true }

        predicate(Unit)

        assertThat(indices.first()).isEqualTo(1)
    }
}