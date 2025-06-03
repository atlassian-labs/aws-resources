package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.ami.UnattendedUpgradesOptOut
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.CanonicalImageIdByNameResolver

/**
 * @since 1.10.1
 */
class CanonicalAmiProvider private constructor(
    private val imageName: String,
    private val avoidUnattendedUpgrades: Boolean
) : AmiProvider {

    override fun provideAmiId(aws: Aws): String {
        val base = ImageNameResolver()
        val provider = if (avoidUnattendedUpgrades) {
            SshAmiMod.Builder(UnattendedUpgradesOptOut())
                .amiProvider(base)
                .build()
        } else {
            base
        }
        return provider.provideAmiId(aws)
    }

    private inner class ImageNameResolver : AmiProvider {
        override fun provideAmiId(aws: Aws): String = CanonicalImageIdByNameResolver.Builder(aws.ec2)
            .region(aws.region)
            .build()
            .invoke(imageName)
    }

    class Builder {
        private val focal = "ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20250508.1"
        private var imageName = focal

        /**
         * Old Ubuntu versions don't have this feature. Trying to remove this feature fails if it doesn't exist.
         * Probably we could solve this problem via feature detection in [UnattendedUpgradesOptOut].
         */
        private var avoidUnattendedUpgrades = true

        /**
         * Might not benefit from version-specific improvements.
         */
        fun imageName(imageName: String) = apply {
            avoidUnattendedUpgrades = false
            this.imageName = imageName
        }

        /**
         * Make sure your [imageName] is Focal or newer.
         * @since 1.16.0
         */
        fun avoidUnattendedUpgrades() = apply {
            this.avoidUnattendedUpgrades = true
        }

        fun focal() = imageName(focal)
            .avoidUnattendedUpgrades()

        fun build(): CanonicalAmiProvider = CanonicalAmiProvider(imageName, avoidUnattendedUpgrades)
    }
}
