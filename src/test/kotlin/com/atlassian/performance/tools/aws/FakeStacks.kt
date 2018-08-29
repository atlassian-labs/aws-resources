package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.StackStatus
import java.util.*

class FakeStacks {
    fun create(
        tags: List<com.amazonaws.services.cloudformation.model.Tag> = emptyList(),
        creationTime: Date = Date(0)
    ): Stack {
        val stack = Stack()
        stack.creationTime = creationTime
        stack.stackStatus = StackStatus.CREATE_COMPLETE.name
        stack.stackName = "stack"
        stack.setTags(tags)
        return stack
    }
}