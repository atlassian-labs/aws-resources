package com.atlassian.performance.tools.aws

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.TextCapacityMediator
import java.time.Duration

object FakeAws {

    fun awsForUnitTests(
        credentialsProvider: AWSCredentialsProvider = FakeAwsCredentialsProvider(),
        batchingCloudformationRefreshPeriod: Duration = Duration.ofSeconds(20)
    ) = Aws.Builder(
        region = Regions.DEFAULT_REGION
    ).credentialsProvider(
        credentialsProvider
    ).regionsWithHousekeeping(regionsWithHousekeeping = listOf(Regions.DEFAULT_REGION)).capacity(
        capacity = TextCapacityMediator(
            Regions.DEFAULT_REGION
        )
    ).batchingCloudformationRefreshPeriod(
        batchingCloudformationRefreshPeriod = batchingCloudformationRefreshPeriod
    ).build()
}