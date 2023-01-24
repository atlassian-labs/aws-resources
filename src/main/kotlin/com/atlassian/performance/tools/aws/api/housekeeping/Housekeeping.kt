package com.atlassian.performance.tools.aws.api.housekeeping

import com.atlassian.performance.tools.aws.api.Aws

interface Housekeeping {
    /**
     * Releases all the expired AWS resources allocated by JPT.
     */
    fun cleanLeftovers(aws: Aws)
}
