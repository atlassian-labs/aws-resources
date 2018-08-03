package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest
import com.amazonaws.services.ec2.model.SecurityGroup
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.CompletableFuture

class Ec2SecurityGroup(
    private val securityGroup: SecurityGroup,
    private val ec2: AmazonEC2
) : Resource {

    private val expiry: Instant? = Investment.parseExpiry(securityGroup.tags.map { Tag(it) })

    override fun isExpired(): Boolean = expiry?.isBefore(now()) ?: false

    override fun release(): CompletableFuture<*> {
        return CompletableFuture.runAsync {
            ec2.deleteSecurityGroup(
                DeleteSecurityGroupRequest().withGroupId(securityGroup.groupId)
            )
        }
    }
}