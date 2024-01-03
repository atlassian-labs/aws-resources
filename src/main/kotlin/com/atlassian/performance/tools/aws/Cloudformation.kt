package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.StackStatus
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.ProvisionedStack
import com.atlassian.performance.tools.aws.api.ScrollingCloudformation
import java.util.function.Consumer

internal class Cloudformation(
    private val aws: Aws,
    private val cloudformation: AmazonCloudFormation
) {
    fun consumeExpiredStacks(call: Consumer<List<ProvisionedStack>>) {
        val cleanStackStatuses = listOf(
            StackStatus.DELETE_COMPLETE,
            StackStatus.DELETE_IN_PROGRESS
        )
        ScrollingCloudformation(cloudformation).scrollThroughStacks { stackBatch ->
            val expiredStacks = stackBatch
                .filter { StackStatus.fromValue(it.stackStatus) !in cleanStackStatuses }
                .map { ProvisionedStack(it, aws) }
                .filter { it.isExpired() }
            call.accept(expiredStacks)
        }
    }

    fun listDisposableStacks(): List<Stack> {
        val stacks = mutableListOf<Stack>()
        ScrollingCloudformation(cloudformation)
            .scrollThroughStacks { batch ->
                stacks.addAll(batch.filter { it.tags.map { it.key }.contains(Investment.disposableKey) })
            }
        return stacks
    }
}