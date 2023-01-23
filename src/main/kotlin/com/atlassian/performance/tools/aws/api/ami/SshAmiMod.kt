package com.atlassian.performance.tools.aws.api.ami

import com.amazonaws.services.ec2.model.CreateImageRequest
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.waiters.WaiterParameters
import com.atlassian.performance.tools.aws.api.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files.createTempDirectory
import java.time.Duration
import java.util.*

class SshAmiMod private constructor(
    private val sshInstanceMod: SshInstanceMod,
    private val amiProvider: AmiProvider,
    private val amiCache: AmiCache,
    private val investment: Investment,
) : AmiProvider {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun provideAmiId(aws: Aws): String {
        val baseAmiId = amiProvider.provideAmiId(aws)
        val cacheKeyTags = cacheKeyTags(baseAmiId)
        val cachedAmiId = amiCache.lookup(cacheKeyTags, aws)
        return if (cachedAmiId != null) {
            waitUntilAvailable(cachedAmiId, aws)
            cachedAmiId
        } else {
            generateNewAmi(baseAmiId, cacheKeyTags, aws)
        }
    }

    private fun cacheKeyTags(baseAmiId: String): Map<String, String> {
        return sshInstanceMod.tag() + mapOf("base-ami-id" to baseAmiId)
    }

    private fun generateNewAmi(
        baseAmiId: String,
        tags: Map<String, String>,
        aws: Aws,
    ): String {
        logger.info("Generating new AMI based on $baseAmiId...")
        logger.debug("New AMI tags: $tags")
        val sshInstance = allocateSshInstance(aws, baseAmiId)
        try {
            logger.debug("Modifying $baseAmiId: $tags...")
            sshInstanceMod.modify(sshInstance)
            logger.debug("Modified $baseAmiId: $tags")
            val newAmiId = createAmi(sshInstance, aws)
            tag(newAmiId, tags, aws)
            waitUntilAvailable(newAmiId, aws)
            return newAmiId
        } finally {
            logger.debug("Releasing resources used for modification of base AMI $baseAmiId: $tags...")
            sshInstance.resource.release().get()
            logger.debug("Releasing resources used for modification of base AMI $baseAmiId: $tags")
        }
    }

    private fun allocateSshInstance(
        aws: Aws,
        amiId: String,
    ): SshInstance {
        val keyPrefix = investment.reuseKey()
        val sshKey = SshKeyFormula(aws.ec2, createTempDirectory(keyPrefix), keyPrefix, investment.lifespan).provision()
        // don't use aws.awaitingEc2, it would cause infinite recursion
        val awaitingEc2 = AwaitingEc2(aws.ec2, aws.terminationBatchingEc2, aws.instanceNanny, amiId)
        return awaitingEc2.allocateInstance(investment, sshKey, vpcId = null) { launch -> launch }
    }

    private fun createAmi(
        sshInstance: SshInstance,
        aws: Aws,
    ): String {
        val moddedAmiName = "jpt-ssh-ami-mod-" + UUID.randomUUID()
        val amiCreation = CreateImageRequest(sshInstance.instance.instanceId, moddedAmiName)
        return aws.ec2.createImage(amiCreation).imageId
    }

    private fun tag(
        amiId: String,
        tagMap: Map<String, String>,
        aws: Aws,
    ) {
        val tags = tagMap.entries.map { (key, value) -> Tag(key, value) }
        val tagging = CreateTagsRequest()
            .withResources(amiId)
            .withTags(tags)
        aws.ec2.createTags(tagging)
    }

    private fun waitUntilAvailable(
        amiId: String,
        aws: Aws,
    ) {
        logger.info("Waiting for $amiId AMI to become available...")
        val waiterParameters = WaiterParameters(DescribeImagesRequest().withImageIds(amiId))
        aws.ec2.waiters().imageAvailable().run(waiterParameters)
        logger.info("The $amiId AMI is available")
    }

    interface SshInstanceMod {
        fun modify(sshInstance: SshInstance)

        /**
         * @return tags, which:
         * - represent the modification
         * - cannot be empty
         * - might be used for finding existing modded AMIs
         */
        fun tag(): Map<String, String>
    }

    interface AmiCache {

        /**
         * @return an existing AMI matching [tags] or null if none match
         */
        fun lookup(
            tags: Map<String, String>,
            aws: Aws,
        ): String?
    }

    class Builder(
        private var sshInstanceMod: SshInstanceMod,
    ) {
        private var amiProvider: AmiProvider = CanonicalAmiProvider.Builder().build()
        private var amiCache: AmiCache = TiebreakingAmiCache.Builder().build()
        private var investment: Investment = Investment(
            useCase = "Modify an AMI image",
            lifespan = Duration.ofMinutes(20),
        )

        fun amiProvider(amiProvider: AmiProvider) = apply { this.amiProvider = amiProvider }
        fun amiCache(amiCache: AmiCache) = apply { this.amiCache = amiCache }
        fun investment(investment: Investment) = apply { this.investment = investment }

        fun build(): SshAmiMod = SshAmiMod(
            sshInstanceMod = sshInstanceMod,
            amiProvider = amiProvider,
            amiCache = amiCache,
            investment = investment,
        )
    }
}
