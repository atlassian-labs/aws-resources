package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.api.Aws

/**
 * @since 1.10.1
 */
class FixedAmiProvider(
    private val imageId: String
) : AmiProvider {

    override fun provideAmiId(aws: Aws): String = imageId
}
