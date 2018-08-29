package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import org.junit.Test
import java.lang.Exception

class ProvisionedStackTest {
    private val awsMock = Aws(
        Regions.DEFAULT_REGION,
        FakeAwsCredentialsProvider()
    )

    @Test
    fun shouldCreateProvisionedStackForStacksWithLifespanTag() {
        ProvisionedStack(
            stack = FakeStacks()
                .create(
                    listOf(
                        Tag("lifespan", "PT10M").toCloudformation()
                    )
                ),
            aws = awsMock
        )
    }

    @Test(expected = Exception::class)
    fun shouldNotCreateProvisionedStackForStacksWithLifespanTag() {
        ProvisionedStack(
            FakeStacks()
                .create(),
            awsMock
        )
    }
}