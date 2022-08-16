package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions

/**
 * Based on https://ubuntu.com/server/docs/cloud-images/amazon-ec2
 */
internal object CanonicalOwnerIdRegistry {
    const val default = "099720109477"
    val byRegion = Regions.values().associate {
        it to when(it) {
            Regions.GovCloud -> "513442679011"
            Regions.CN_NORTH_1,
            Regions.CN_NORTHWEST_1 -> "837727238323"
            else -> default
        }
    }

    fun forRegion(region: Regions?) = byRegion[region] ?: default
}