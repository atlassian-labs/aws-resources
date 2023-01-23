package com.atlassian.performance.tools.aws.api

import org.junit.jupiter.api.Test
import java.time.Duration

class SshKeyNameTest {

    @Test
    fun startsWithPrefixFollowedByDelimiter() {
        val sshKeyName = SshKeyName("some-prefix", Duration.ofMinutes(45))
        assert(sshKeyName.name.startsWith("some-prefix${SshKeyName.DELIMITER}"))
    }

    @Test
    fun parseExpiry() {
        val sshKeyName = SshKeyName("some-prefix", Duration.ofMinutes(15))
        assert(sshKeyName.expiry != null)
    }

    @Test
    fun failToParseExpiryWhenWrongDelimiter() {
        val sshKeyName = SshKeyName("some-prefix-2017-10-02T10:52:58.292Z")
        assert(sshKeyName.expiry == null)
    }

    @Test
    fun failToParseExpiryWhenNoExpiry() {
        val sshKeyName = SshKeyName("some-prefix")
        assert(sshKeyName.expiry == null)
    }
}