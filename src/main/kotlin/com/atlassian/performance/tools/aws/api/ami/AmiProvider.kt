package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.api.Aws

interface AmiProvider {

    fun provideAmiId(aws: Aws): String
}
