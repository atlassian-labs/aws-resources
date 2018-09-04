package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import com.amazonaws.services.cloudformation.model.Stack
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.Tag
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
            FakeStacks().create()
        )
        val cloudformation = Cloudformation(awsMock, CloudformationMock(stacks))

        val expiredStacks = cloudformation.listExpiredStacks()

        assertThat(expiredStacks, empty())
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

        val expiredStacks = cloudformation.listExpiredStacks()

        assertThat(expiredStacks, hasSize(1))
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

        val expiredStacks = cloudformation.listExpiredStacks()

        assertThat(expiredStacks, empty())
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