package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.StackStatus

internal class Cloudformation(
    private val aws: Aws,
    private val cloudformation: AmazonCloudFormation
) {
    fun listExpiredStacks(): List<ProvisionedStack> {
        val cleanStackStatuses = listOf(
            StackStatus.DELETE_COMPLETE,
            StackStatus.DELETE_IN_PROGRESS
        )
        val scrollingCloudformation = ScrollingCloudformation(cloudformation)
        val stacks = mutableListOf<ProvisionedStack>()
        scrollingCloudformation.scrollThroughStacks { stackBatch ->
            stackBatch
                .filter { StackStatus.fromValue(it.stackStatus) !in cleanStackStatuses }
                .forEach { stacks += ProvisionedStack(it, aws) }
        }
        return stacks.filter {
            it.isExpired()
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