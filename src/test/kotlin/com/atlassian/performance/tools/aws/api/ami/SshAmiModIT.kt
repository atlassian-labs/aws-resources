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
import java.util.*

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
        val sshAmiMod = SshAmiMod.Builder(echo)
            .amiCache(NoAmiCache())
            .build()

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

    @Test
    fun shouldReuseCachedAmi() {
        // given
        val uniqueTags = mapOf(
            "cache-key1" to UUID.randomUUID().toString(),
            "cache-key2" to UUID.randomUUID().toString(),
        )
        val brandNewMod = object : SshInstanceMod {
            var amisCreated = 0

            override fun modify(sshInstance: SshInstance) {
                amisCreated++
            }

            override fun tag() = uniqueTags
        }
        val sshAmiMod = SshAmiMod.Builder(brandNewMod)
            .amiCache(TiebreakingAmiCache.Builder().build())
            .build()

        // when
        val firstImageId = sshAmiMod.provideAmiId(aws)
        val secondImageId = sshAmiMod.provideAmiId(aws)

        // then
        assertThat(secondImageId).isEqualTo(firstImageId)
        assertThat(brandNewMod.amisCreated).isEqualTo(1)
    }
}
