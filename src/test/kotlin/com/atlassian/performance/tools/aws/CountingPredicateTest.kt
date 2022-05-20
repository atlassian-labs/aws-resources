package com.atlassian.performance.tools.aws

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class CountingPredicateTest {
    @Test
    fun countingStartsFrom1() {
        val indices = mutableListOf<Int>()
        val predicate = CountingPredicate<Unit> { index, _ -> indices.add(index); true  }

        predicate(Unit)

        assertThat(indices.first(), equalTo(1))
    }
}