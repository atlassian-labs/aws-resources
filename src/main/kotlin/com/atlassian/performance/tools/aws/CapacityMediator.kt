package com.atlassian.performance.tools.aws

interface CapacityMediator {

    /**
     * Mediates a capacity bump.
     *
     * @param limitType static human-readable description of the limit type
     * @param desiredLimit calculates the desired new limit
     * @return bump response
     */
    fun bump(
        limitType: String,
        desiredLimit: () -> Int
    ): String
}