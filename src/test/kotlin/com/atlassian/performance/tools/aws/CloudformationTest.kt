package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.StackStatus
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration.ofMinutes
import java.time.Instant.now
import java.util.*

class CloudformationTest {
    private val awsMock = Aws(
        Regions.DEFAULT_REGION,
        FakeAwsCredentialsProvider()
    )

    @Test
    fun shouldNotListStacksWithoutLifespanTag() {
        val stacks: List<Stack> = listOf(
            createStack(
                Date(0)
            )
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        val expiredStacks = cloudformation.listExpiredStacks()

        assertThat(expiredStacks, empty())
    }

    @Test
    fun shouldListExpiredStacksWithLifespanTag() {
        val twoMinutesAgo = Date(now().minus(ofMinutes(2)).toEpochMilli())

        val stacks: List<Stack> = listOf(
            createStack(
                twoMinutesAgo,
                listOf(
                    Tag("lifespan", "PT1M").toCloudformation()
                )
            )
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        val expiredStacks = cloudformation.listExpiredStacks()

        assertThat(expiredStacks, hasSize(1))
    }

    @Test
    fun shouldNotListStacksWithNotExpired() {
        val twoMinutesAgo = Date(now().minus(ofMinutes(2)).toEpochMilli())
        val stacks: List<Stack> = listOf(
            createStack(
                twoMinutesAgo,
                listOf(
                    Tag("lifespan", "PT10M").toCloudformation()
                )
            )
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        val expiredStacks = cloudformation.listExpiredStacks()

        assertThat(expiredStacks, empty())
    }

    private fun createStack(
        creationTime: Date,
        tags: List<com.amazonaws.services.cloudformation.model.Tag> = emptyList()
    ): Stack {
        val stack = Stack()
        stack.creationTime = creationTime
        stack.stackStatus = StackStatus.CREATE_COMPLETE.name
        stack.stackName = "stack"
        stack.setTags(tags)
        return stack
    }

    private class CloudformationMock(
        private val stacks: Collection<Stack>
    ) : AmazonCloudFormation by FakeCloudformation() {
        override fun describeStacks(describeStacksRequest: DescribeStacksRequest?): DescribeStacksResult {
            val describeStacksResult = DescribeStacksResult()
            describeStacksResult.setStacks(stacks)
            return describeStacksResult
        }
    }
}