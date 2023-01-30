package com.atlassian.performance.tools.aws.ami

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.DeregisterImageRequest
import com.amazonaws.services.ec2.model.Image
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.Resource
import com.atlassian.performance.tools.aws.api.Tag
import java.time.Instant.now
import java.util.concurrent.CompletableFuture

internal class AmiImage(
    private val image: Image,
    private val ec2: AmazonEC2
) : Resource {
    private val expiry = Investment.parseExpiry(image.tags.map { Tag(it) })
    override fun isExpired() = expiry?.isBefore(now()) ?: false

    override fun release(): CompletableFuture<*> {
        return CompletableFuture.supplyAsync { ec2.deregisterImage(DeregisterImageRequest(image.imageId)) }
    }
}
