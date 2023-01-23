package com.atlassian.performance.tools.aws.api

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.atlassian.performance.tools.aws.FakeAws
import com.atlassian.performance.tools.aws.FakeAwsCredentialsProvider
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.time.Duration
import java.util.concurrent.TimeUnit.SECONDS

class StackFormulaTest {
    private class MissingCredentialsProvider : AWSCredentialsProvider by FakeAwsCredentialsProvider() {
        override fun getCredentials(): AWSCredentials {
            throw Exception("Credentials are missing")
        }
    }

    @Test
    @Timeout(5, unit = SECONDS)
    fun shouldNotHang() {
        val formula = StackFormula(
            investment = Investment(
                useCase = "Unit test the StackFormula",
                lifespan = Duration.ofMinutes(1)
            ),
            aws = FakeAws.awsForUnitTests(
                batchingCloudformationRefreshPeriod = Duration.ofMillis(100),
                credentialsProvider = MissingCredentialsProvider()
            ),
            cloudformationTemplate = "malformed YAML content",
            detectionTimeout = Duration.ofMillis(500),
            pollingTimeout = Duration.ofSeconds(2)
        )

        assertThatThrownBy {
            formula.provision()
        }
    }
}
