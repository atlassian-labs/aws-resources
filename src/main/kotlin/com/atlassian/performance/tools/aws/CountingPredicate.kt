package com.atlassian.performance.tools.aws

internal class CountingPredicate<T>(
    private val predicate: (Int, T) -> Boolean
) : (T) -> Boolean {
    private var counter = 0;
    override fun invoke(page: T) = predicate(++counter, page)
}