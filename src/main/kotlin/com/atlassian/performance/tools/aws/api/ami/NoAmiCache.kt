package com.atlassian.performance.tools.aws.api.ami

import com.atlassian.performance.tools.aws.api.Aws
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class NoAmiCache : SshAmiMod.AmiCache {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun lookup(
        tags: Map<String, String>,
        aws: Aws
    ): String? {
        logger.debug("Skipping AMI cache")
        return null
    }
}
