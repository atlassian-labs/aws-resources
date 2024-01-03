package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import com.amazonaws.services.cloudformation.model.Stack
import com.atlassian.performance.tools.aws.api.Tag
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.time.Duration.ofMinutes
import java.time.Instant.now
import java.util.*
import java.util.function.Consumer

class CloudformationTest {
    private val awsMock = FakeAws.awsForUnitTests()

    @Test
    fun shouldNotListStacksWithoutLifespanTag() {
        val stacks: List<Stack> = listOf(
            FakeStacks().create()
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        cloudformation.consumeExpiredStacks(Consumer { expiredStacks ->
            assertThat(expiredStacks).isEmpty()
        })
    }

    @Test
    fun shouldListExpiredStacksWithLifespanTag() {
        val twoMinutesAgo = Date(now().minus(ofMinutes(2)).toEpochMilli())

        val stacks: List<Stack> = listOf(
            FakeStacks().create(
                listOf(
                    Tag("lifespan", "PT1M").toCloudformation()
                ),
                twoMinutesAgo
            )
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        cloudformation.consumeExpiredStacks(Consumer { expiredStacks ->
            assertThat(expiredStacks).hasSize(1)
        })
    }

    @Test
    fun shouldNotListStacksWithNotExpired() {
        val twoMinutesAgo = Date(now().minus(ofMinutes(2)).toEpochMilli())
        val stacks: List<Stack> = listOf(
            FakeStacks().create(
                listOf(
                    Tag("lifespan", "PT10M").toCloudformation()
                ),
                twoMinutesAgo
            )
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        cloudformation.consumeExpiredStacks(Consumer { expiredStacks ->
            assertThat(expiredStacks).isEmpty()
        })
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