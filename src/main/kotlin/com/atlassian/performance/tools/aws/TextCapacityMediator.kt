package com.atlassian.performance.tools.aws

class TextCapacityMediator : CapacityMediator {

    override fun bump(
        limitType: String,
        desiredLimit: () -> Int
    ): String {
        return "You need to bump $limitType to ${desiredLimit()}"
    }
}
