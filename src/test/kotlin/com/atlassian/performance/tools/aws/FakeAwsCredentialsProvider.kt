package com.atlassian.performance.tools.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider

class FakeAwsCredentialsProvider : AWSCredentialsProvider {
    override fun getCredentials(): AWSCredentials {
        throw Exception("unexpected call")
    }

    override fun refresh() {
        throw Exception("unexpected call")
    }
}