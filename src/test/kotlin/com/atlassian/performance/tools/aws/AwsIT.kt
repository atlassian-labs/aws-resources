package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.StackStatus.*
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.StackFormula
import com.atlassian.performance.tools.io.api.readResourceText
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.experimental.categories.Category
import java.nio.file.Files
import java.time.Duration
import java.util.*

class AwsIT {

    @Test
    fun shouldCleanUpAfterProvisioning() {
        val aws = IntegrationTestRuntime.aws
        val lifespan = Duration.ofMinutes(5)
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
        stack.release().get()

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

    @Test
    fun shouldTransferViaStorage() {
        val textFile = Files.createTempFile("AwsIT-", ".txt")
            .toFile()
            .also { it.writeText("beam me up") }

        val storage = IntegrationTestRuntime.aws.resultsStorage("aws-resources-test-${UUID.randomUUID()}")
        storage.upload(textFile)
        val downloaded = storage.download(Files.createTempDirectory("AwsIT-"))

        assertThat(downloaded.resolve(textFile.name)).hasContent("beam me up")
    }
}