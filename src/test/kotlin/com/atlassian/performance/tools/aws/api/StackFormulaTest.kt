package com.atlassian.performance.tools.aws.api

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.atlassian.performance.tools.aws.FakeAws
import com.atlassian.performance.tools.aws.FakeAwsCredentialsProvider
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration

class StackFormulaTest {
    private class MissingCredentialsProvider : AWSCredentialsProvider by FakeAwsCredentialsProvider() {
        override fun getCredentials(): AWSCredentials {
            throw Exception("Credentials are missing")
        }
    }

    @Test(timeout = 5000)
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

        val exception: Exception? = try {
            formula.provision()
            null
        } catch (e: Exception) {
            e
        }

        assertThat("should fail for some reason", exception, notNullValue())
    }
}
