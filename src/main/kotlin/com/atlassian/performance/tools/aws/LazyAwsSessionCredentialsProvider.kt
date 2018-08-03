package com.atlassian.performance.tools.aws

import com.amazonaws.auth.AWSSessionCredentials
import com.amazonaws.auth.AWSSessionCredentialsProvider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class LazyAwsSessionCredentialsProvider(
    private val sessionCredentialsProvider: AWSSessionCredentialsProvider
) : AWSSessionCredentialsProvider {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    private val lazyCredentials by lazy {
        sessionCredentialsProvider.credentials
    }

    override fun getCredentials(): AWSSessionCredentials {
        return lazyCredentials
    }

    override fun refresh() {
        logger.info("Refused to refresh the credentials. Make sure your AWS interactions are fast enough to fit.")
    }
}