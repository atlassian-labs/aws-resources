package com.atlassian.performance.tools.aws.api.ami

import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.Filter
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.ami.SshAmiMod.AmiCache
import com.atlassian.performance.tools.aws.api.ami.tiebreaker.AmiTiebreaker
import com.atlassian.performance.tools.aws.api.ami.tiebreaker.NewestAvailableAmi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class TiebreakingAmiCache private constructor(
    private val tiebreaker: AmiTiebreaker,
) : AmiCache {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun lookup(
        tags: Map<String, String>,
        aws: Aws,
    ): String? {
        val filters = tags.map { (key, value) -> Filter("tag:$key", listOf(value)) }
        val amis = aws.ec2.describeImages(DescribeImagesRequest().withFilters(filters)).images
        logger.debug("Cached AMIs (${amis.size}): $amis")
        val ami = tiebreaker.pick(amis)
        return if (ami != null) {
            logger.debug("Reusing cached AMI: $ami")
            ami.imageId
        } else {
            logger.info("No AMI matches tags $tags")
            null
        }
    }

    class Builder {
        private var tiebreaker: AmiTiebreaker = NewestAvailableAmi()

        fun tiebreaker(tiebreaker: AmiTiebreaker) = apply { this.tiebreaker = tiebreaker }

        fun build(): AmiCache = TiebreakingAmiCache(tiebreaker)
    }
}
