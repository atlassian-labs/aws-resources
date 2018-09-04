package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import com.amazonaws.services.cloudformation.model.Stack
import com.atlassian.performance.tools.aws.FakeCloudformation
import com.atlassian.performance.tools.aws.FakeStacks
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers
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

    @Test
    fun shouldReturnOnlyProvisionedStacks() {
        val fakeStacks = FakeStacks()
        val stackWithLifespan = fakeStacks.create(
            listOf(
                Tag("lifespan", "PT1M").toCloudformation()
            )
        )
        val awsStacks: List<Stack> = listOf(
            fakeStacks.create(),
            fakeStacks.create(
                listOf(
                    Tag("tag", "value").toCloudformation()
                )
            ),
            stackWithLifespan
        )
        val scrollingCloudformation = ScrollingCloudformation(
            StacksCloudformation(
                awsStacks
            )
        )

        val scrolledStacks = mutableListOf<Stack>()
        scrollingCloudformation.scrollThroughStacks { scrolledStacks += it }

        assertThat(scrolledStacks, Matchers.contains(stackWithLifespan))
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

private class StacksCloudformation(
    private val stacks: List<Stack>
) : AmazonCloudFormation by FakeCloudformation() {
    override fun describeStacks(describeStacksRequest: DescribeStacksRequest?): DescribeStacksResult {
        return DescribeStacksResult()
            .withStacks(stacks)
            .withNextToken(null)
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