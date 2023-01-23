package com.atlassian.performance.tools.aws.api

import com.atlassian.performance.tools.aws.FakeAws
import com.atlassian.performance.tools.aws.FakeStacks
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

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

    @Test
    fun shouldNotCreateProvisionedStackForStacksWithLifespanTag() {
        assertThatThrownBy {
            ProvisionedStack(
                FakeStacks()
                    .create(),
                FakeAws.awsForUnitTests()
            )
        }
    }
}