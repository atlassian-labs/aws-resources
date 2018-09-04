package com.atlassian.performance.tools.aws.api

import com.atlassian.performance.tools.aws.api.CapacityMediator

class TextCapacityMediator : CapacityMediator {

    override fun bump(
        limitType: String,
        desiredLimit: () -> Int
    ): String {
        return "You need to bump $limitType to ${desiredLimit()}"
    }
}
