package com.atlassian.performance.tools.aws.api

import com.atlassian.performance.tools.aws.FakeAws
import com.atlassian.performance.tools.aws.FakeStacks
import org.junit.Test

class ProvisionedStackTest {

    @Test
    fun shouldCreateProvisionedStackForStacksWithLifespanTag() {
        ProvisionedStack(
            stack = FakeStacks()
                .create(
                    listOf(
                        Tag("lifespan", "PT10M").toCloudformation()
                    )
                ),
            aws = FakeAws.awsForUnitTests()
        )
    }

    @Test(expected = Exception::class)
    fun shouldNotCreateProvisionedStackForStacksWithLifespanTag() {
        ProvisionedStack(
            FakeStacks()
                .create(),
            FakeAws.awsForUnitTests()
        )
    }
}