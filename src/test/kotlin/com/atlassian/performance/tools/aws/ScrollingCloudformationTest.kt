package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class ScrollingCloudformationTest {

    @Test(timeout = 2000)
    fun shouldMakeOneRequestForOneBatch() {
        shouldMakeOneRequestPerBatch(1)
    }

    @Test(timeout = 2000)
    fun shouldMakeTwoRequestsForTwoBatches() {
        shouldMakeOneRequestPerBatch(2)
    }

    @Test(timeout = 2000)
    fun shouldMakeThreeRequestsForThreeBatches() {
        shouldMakeOneRequestPerBatch(3)
    }

    private fun shouldMakeOneRequestPerBatch(
        batchCount: Int
    ) {
        val cloudformation = DescribingCloudformation(batchCount)
        val scrollingCloudformation = ScrollingCloudformation(cloudformation)

        scrollingCloudformation.scrollThroughStacks {}

        assertThat(cloudformation.countRequests(), equalTo(batchCount))
    }
}

/**
 * Supports describing stacks with scrolling. The token directly encodes the batch number.
 * Hardcoded with a maximum number of batches.
 */
private class DescribingCloudformation(
    private val batchCount: Int
) : AmazonCloudFormation by FakeCloudformation() {

    private var requestCount = 0

    override fun describeStacks(
        describeStacksRequest: DescribeStacksRequest?
    ): DescribeStacksResult {
        requestCount++
        val batch = describeStacksRequest!!.nextToken?.toInt() ?: 1
        val nextToken = when {
            batch < batchCount -> (batch + 1).toString()
            else -> null
        }
        return DescribeStacksResult()
            .withStacks(emptyList())
            .withNextToken(nextToken)
    }

    fun countRequests() = requestCount
}