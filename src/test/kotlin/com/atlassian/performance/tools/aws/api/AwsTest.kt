package com.atlassian.performance.tools.aws.api

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.atlassian.performance.tools.aws.FakeAws
import com.atlassian.performance.tools.aws.FakeAwsCredentialsProvider
import org.junit.Test

class AwsTest {
    private class MyAwsCredentialsProvider : AWSCredentialsProvider by FakeAwsCredentialsProvider() {
        override fun getCredentials(): AWSCredentials {
            throw Exception("You shouldn't use credentials in Aws constructor")
        }
    }

    @Test
    fun shouldNotUseCredentialsInConstructor() {
        FakeAws.awsForUnitTests(credentialsProvider = MyAwsCredentialsProvider())
    }
}