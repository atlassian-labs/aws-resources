package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.StackStatus.*
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification
import com.atlassian.performance.tools.aws.IntegrationTestRuntime.aws
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.SshKeyFormula
import com.atlassian.performance.tools.aws.api.StackFormula
import com.atlassian.performance.tools.io.api.readResourceText
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.experimental.categories.Category
import java.nio.file.Files
import java.time.Duration
import java.util.*

class AwsIT {

    private val investment = Investment(
        useCase = "Test aws-resources library",
        lifespan = Duration.ofMinutes(5),
        disposable = true
    )
    private val workspace = Files.createTempDirectory("AwsIT-")
    private val awsPrefix = "aws-resources-test-"

    @Test
    fun shouldCleanUpAfterProvisioning() {
        val aws = aws
        val stackFormula = StackFormula(
            investment = investment,
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
        aws.cleanLeftovers()
    }

    @Test
    fun shouldTransferViaStorage() {
        val textFile = Files.createTempFile("AwsIT-", ".txt")
            .toFile()
            .also { it.writeText("beam me up") }

        val storage = aws.resultsStorage("aws-resources-test-${UUID.randomUUID()}")
        storage.upload(textFile)
        val downloaded = storage.download(workspace)

        assertThat(downloaded.resolve(textFile.name)).hasContent("beam me up")
    }

    @Test
    fun shouldTransferViaStorageOnEc2() {
        val sshKey = SshKeyFormula(aws.ec2, workspace, awsPrefix, investment.lifespan).provision()
        val sshInstance = aws
            .awaitingEc2
            .allocateInstance(investment, sshKey, vpcId = null) { launch ->
                launch.withIamInstanceProfile(
                    IamInstanceProfileSpecification().withName(aws.shortTermStorageAccess())
                )
            }
        val storage = aws.virtualUsersStorage(awsPrefix + UUID.randomUUID())
        val location = storage.location

        sshInstance.ssh.newConnection().use { ssh ->
            ssh.execute("echo 'shoot for the moon' > local.txt")
            ssh.execute("aws s3 cp --region=${location.regionName} local.txt ${location.uri}/remote.txt")
        }

        val downloaded = storage.download(workspace)
        assertThat(downloaded.resolve("remote.txt")).hasContent("shoot for the moon")
        sshInstance.resource.release().get()
        sshKey.remote.release().get()
    }
}