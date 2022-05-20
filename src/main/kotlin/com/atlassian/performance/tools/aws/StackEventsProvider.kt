package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.StackEvent

internal interface StackEventsProvider {
    fun getEvents(stackId: String): List<StackEvent>?
}