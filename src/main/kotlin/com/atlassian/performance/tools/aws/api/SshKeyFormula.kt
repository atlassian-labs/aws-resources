package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.CreateKeyPairRequest
import com.atlassian.performance.tools.io.api.copy
import com.atlassian.performance.tools.io.api.resolveSafely
import java.nio.file.Path
import java.time.Duration

class SshKeyFormula(
    private val ec2: AmazonEC2,
    private val workingDirectory: Path,
    private val prefix: String,
    private val lifespan: Duration
) {
    fun provision(): SshKey {
        val sshKeyName = SshKeyName(prefix, lifespan)
        val keyPair = ec2
            .createKeyPair(CreateKeyPairRequest(sshKeyName.name))
            .keyPair

        val keyFile = workingDirectory.resolveSafely(path = "${keyPair.keyName}.pem")
        keyPair.keyMaterial.byteInputStream().copy(keyFile)

        val remoteSshKey = RemoteSshKey(sshKeyName, ec2)
        val localSshKey = SshKeyFile(keyFile)
        localSshKey.fixPermissions()

        return SshKey(localSshKey, remoteSshKey)
    }
}