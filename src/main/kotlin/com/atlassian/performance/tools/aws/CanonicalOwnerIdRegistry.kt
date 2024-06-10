package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Regions.*

/**
 * Based on https://documentation.ubuntu.com/aws/en/latest/aws-how-to/instances/find-ubuntu-images/#ownership-verification
 */
internal object CanonicalOwnerIdRegistry {
    fun forRegion(region: Regions?) = when (region) {
        GovCloud -> "513442679011"
        CN_NORTH_1, CN_NORTHWEST_1 -> "837727238323"
        else -> "099720109477"
    }
}