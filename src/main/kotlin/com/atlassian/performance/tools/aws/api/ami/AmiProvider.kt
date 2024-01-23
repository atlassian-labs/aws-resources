package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.api.Aws

/**
 * @since 1.10.1
 */
interface AmiProvider {

    fun provideAmiId(aws: Aws): String
}
