package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.StackStatus.*
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.StackFormula
import com.atlassian.performance.tools.io.api.readResourceText
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.experimental.categories.Category
import java.time.Duration

class AwsIT {

    @Test
    fun shouldCleanUpAfterProvisioning() {
        val aws = IntegrationTestRuntime.aws
        val lifespan = Duration.ofSeconds(1)
        val stackFormula = StackFormula(
            investment = Investment(
                useCase = "Test housekeeping",
                lifespan = lifespan,
                disposable = true
            ),
            cloudformationTemplate = readResourceText("aws/short-term-storage.yaml"),
            aws = aws
        )

        val stack = stackFormula.provision()
        Thread.sleep(lifespan.toMillis())
        aws.cleanLeftovers()

        aws
            .listDisposableStacks()
            .find { it.stackName == stack.stackName }
            ?.let { fromValue(it.stackStatus) }
            ?.let { assertThat(it).isIn(DELETE_IN_PROGRESS, DELETE_COMPLETE) }
    }

    @Test
    @Category(CleanLeftovers::class)
    fun shouldCleanLeftovers() {
        IntegrationTestRuntime.aws.cleanLeftovers()
    }
}