package com.atlassian.performance.tools.aws

import com.atlassian.performance.tools.ssh.Ssh

data class SshInstance(
    val ssh: Ssh,
    val resource: Resource
)
