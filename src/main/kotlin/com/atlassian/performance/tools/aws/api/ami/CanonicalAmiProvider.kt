package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.CanonicalImageIdByNameResolver

class CanonicalAmiProvider private constructor(
    private val imageName: String,
) : AmiProvider {

    override fun provideAmiId(aws: Aws): String {
        return CanonicalImageIdByNameResolver.Builder(aws.ec2)
            .region(aws.region)
            .build()
            .invoke(imageName)
    }

    class Builder {
        private val focal = "ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20220610"
        private var imageName = focal

        fun imageName(imageName: String) = apply { this.imageName = imageName }

        fun focal() = imageName(focal)

        fun build(): CanonicalAmiProvider = CanonicalAmiProvider(imageName)
    }
}
