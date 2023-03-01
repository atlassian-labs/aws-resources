package com.atlassian.performance.tools.aws.ami

import com.atlassian.performance.tools.aws.api.SshInstance
import com.atlassian.performance.tools.aws.api.ami.SshAmiMod
import com.atlassian.performance.tools.jvmtasks.api.IdempotentAction
import com.atlassian.performance.tools.jvmtasks.api.StaticBackoff
import java.time.Duration
import java.time.Duration.ofSeconds

/**
 * This Ubuntu feature runs apt commands in the background, messing with apt locks.
 */
internal class UnattendedUpgradesOptOut : SshAmiMod.SshInstanceMod {
    override val expectedDuration: Duration = Duration.ofMinutes(1)
    override val useCase = "Avoid unattended-upgrades"

    override fun modify(sshInstance: SshInstance) {
        sshInstance.ssh.newConnection().use { ssh ->
            IdempotentAction("remove unattended upgrades") {
                ssh.execute("sudo apt remove unattended-upgrades -y")
            }.retry(2, StaticBackoff(ofSeconds(10)))
        }
    }

    override fun tag() = mapOf("unattended-upgrades-removed" to "true")
}
