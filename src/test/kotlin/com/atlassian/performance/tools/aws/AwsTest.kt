package com.atlassian.performance.tools.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import org.junit.Test

class AwsTest {
    private class MyAwsCredentialsProvider : AWSCredentialsProvider by FakeAwsCredentialsProvider() {
        override fun getCredentials(): AWSCredentials {
            throw Exception("You shouldn't use credentials in Aws constructor")
        }
    }

    @Test
    fun shouldNotUseCredentialsInConstructor() {
        Aws(Regions.DEFAULT_REGION, MyAwsCredentialsProvider())
    }
}