package com.atlassian.performance.tools.aws

import com.atlassian.performance.tools.ssh.api.Ssh

data class SshInstance(
    val ssh: Ssh,
    val resource: Resource
)
