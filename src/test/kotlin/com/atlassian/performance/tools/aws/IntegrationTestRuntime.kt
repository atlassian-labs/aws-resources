package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions.EU_WEST_3
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.TextCapacityMediator
import java.time.Duration

object IntegrationTestRuntime {
    private val region = EU_WEST_3
    val aws = Aws.Builder(
        region = region
    ).regionsWithHousekeeping(
        regionsWithHousekeeping = listOf(EU_WEST_3) // if you change this, make sure you set up housekeeping there!
    ).capacity(
        capacity = TextCapacityMediator(region)
    ).batchingCloudformationRefreshPeriod(batchingCloudformationRefreshPeriod = Duration.ofSeconds(5)).build()
}
