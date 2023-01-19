package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.IntegrationTestRuntime.aws
import com.atlassian.performance.tools.aws.api.SshInstance
import com.atlassian.performance.tools.aws.api.ami.SshAmiMod.SshInstanceMod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SshAmiModIT {

    @Test
    fun shouldEchoToFile() {
        val echo = object : SshInstanceMod {
            override fun modify(sshInstance: SshInstance) {
                sshInstance.ssh.newConnection().use { it.execute("echo kebab > some-file.txt") }
            }

            override fun tag() = mapOf(
                "echo-content" to "kebab",
                "echo-file" to "some-file.txt",
            )
        }
        val amiProvider = SshAmiMod.Builder(echo).build()

        val imageId = amiProvider.provideAmiId(aws)

        assertThat(imageId).isNotEmpty
    }
}
