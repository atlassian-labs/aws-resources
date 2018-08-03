package com.atlassian.performance.tools.aws

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.time.Instant

class SshKeyName(
    val name: String
) {
    companion object {
        val DELIMITER = "--"
    }

    private val logger: Logger = LogManager.getLogger(this::class.java)

    constructor(prefix: String, lifespan: Duration) :
        this(prefix + DELIMITER + (Instant.now() + lifespan).toString())

    val expiry: Instant? = try {
        Instant.parse(name.substringAfterLast(DELIMITER))
    } catch (e: Exception) {
        logger.debug("Failed to parse expiration time from key $name. This key will be treated as expired.")
        null
    }
}