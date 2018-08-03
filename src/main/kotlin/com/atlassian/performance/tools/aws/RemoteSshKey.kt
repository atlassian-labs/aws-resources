package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Instant
import java.util.concurrent.CompletableFuture

class RemoteSshKey(
    nameWithExpiry: SshKeyName,
    private val ec2: AmazonEC2
) : Resource {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    val name: String = nameWithExpiry.name
    private val expiry: Instant? = nameWithExpiry.expiry

    override fun isExpired(): Boolean = expiry?.isBefore(Instant.now()) ?: true

    override fun release(): CompletableFuture<*> {
        return CompletableFuture.runAsync {
            logger.debug("Deleting key pair $name")
            ec2.deleteKeyPair(DeleteKeyPairRequest().withKeyName(name))
        }
    }
}