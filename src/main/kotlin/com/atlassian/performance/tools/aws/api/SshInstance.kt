package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.ec2.model.Instance
import com.atlassian.performance.tools.ssh.api.Ssh

data class SshInstance(
    val ssh: Ssh,
    val resource: Resource,
    val instance: Instance
)
