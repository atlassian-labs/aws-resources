package com.atlassian.performance.tools.aws.api.ami

import com.amazonaws.services.ec2.model.CreateImageRequest
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.Tag
import com.atlassian.performance.tools.aws.api.*
import java.nio.file.Files.createTempDirectory
import java.time.Duration
import java.util.*

class SshAmiMod private constructor(
    private val sshInstanceMod: SshInstanceMod,
    private val amiProvider: AmiProvider,
    private val investment: Investment,
) : AmiProvider {

    override fun provideAmiId(aws: Aws): String {
        val baseAmiId = amiProvider.provideAmiId(aws)
        val keyPrefix = investment.reuseKey()
        val sshKey = SshKeyFormula(aws.ec2, createTempDirectory(keyPrefix), keyPrefix, investment.lifespan).provision()
        // don't use aws.awaitingEc2, it would cause infinite recursion
        val awaitingEc2 = AwaitingEc2(aws.ec2, aws.terminationBatchingEc2, aws.instanceNanny, baseAmiId)
        val sshInstance = awaitingEc2.allocateInstance(investment, sshKey, vpcId = null) { launch -> launch }
        try {
            sshInstanceMod.modify(sshInstance)
            val moddedAmiId = createAmi(sshInstance, aws)
            tag(moddedAmiId, aws)
            return moddedAmiId
        } finally {
            sshInstance.resource.release().get()
        }
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
        aws: Aws,
    ) {
        val tags = sshInstanceMod
            .tag()
            .entries
            .map { (key, value) -> Tag(key, value) }
        val tagging = CreateTagsRequest()
            .withResources(amiId)
            .withTags(tags)
        aws.ec2.createTags(tagging)
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

    class Builder(
        private var sshInstanceMod: SshInstanceMod,
    ) {
        private var amiProvider: AmiProvider = CanonicalAmiProvider.Builder().build()
        private var investment: Investment = Investment(
            useCase = "Modify an AMI image",
            lifespan = Duration.ofMinutes(20),
        )

        fun amiProvider(amiProvider: AmiProvider) = apply { this.amiProvider = amiProvider }
        fun investment(investment: Investment) = apply { this.investment = investment }

        fun build(): SshAmiMod = SshAmiMod(
            sshInstanceMod = sshInstanceMod,
            amiProvider = amiProvider,
            investment = investment,
        )
    }
}
