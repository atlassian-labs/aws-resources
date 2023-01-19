package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.IntegrationTestRuntime.aws
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.SshInstance
import com.atlassian.performance.tools.aws.api.SshKeyFormula
import com.atlassian.performance.tools.aws.api.ami.SshAmiMod.SshInstanceMod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files.createTempDirectory
import java.time.Duration

class SshAmiModIT {

    @Test
    fun shouldEchoToFile() {
        // given
        val echo = object : SshInstanceMod {
            override fun modify(sshInstance: SshInstance) {
                sshInstance.ssh.newConnection().use { it.execute("echo kebab > some-file.txt") }
            }

            override fun tag() = mapOf(
                "echo-content" to "kebab",
                "echo-file" to "some-file.txt",
            )
        }
        val sshAmiMod = SshAmiMod.Builder(echo).build()

        // when
        val newImageId = sshAmiMod.provideAmiId(aws)

        // then
        val investment = Investment("SshAmiModIT.shouldEchoToFile", Duration.ofMinutes(10))
        val prefix = investment.reuseKey()
        val sshKey = SshKeyFormula(aws.ec2, createTempDirectory(prefix), prefix, investment.lifespan).provision()
        aws.awaitingEc2.allocateInstance(investment, sshKey, vpcId = null) { launch ->
            launch.withImageId(newImageId)
        }.ssh.newConnection().use { ssh ->
            val actualContent = ssh.safeExecute("cat some-file.txt").output
            assertThat(actualContent).isEqualToIgnoringWhitespace("kebab")
        }
    }
}
