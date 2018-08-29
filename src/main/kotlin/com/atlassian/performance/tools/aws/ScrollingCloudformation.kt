package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import com.amazonaws.services.cloudformation.model.Stack

/**
 * Scrolls through batches of AWS CloudFormation stacks.
 */
class ScrollingCloudformation(
    private val cloudformation: AmazonCloudFormation
) {
    /**
     * Scrolls through stacks. Skip stacks that were not provisioned by JPT.
     */
    fun scrollThroughStacks(
        batchAction: (List<Stack>) -> Unit
    ) {
        var token: String? = null
        do {
            val response = cloudformation.describeStacks(
                DescribeStacksRequest().withNextToken(token)
            )
            batchAction(
                filterOutStacksNotProvisionedByJpt(response)
            )
            token = response.nextToken
        } while (token != null)
    }

    private fun filterOutStacksNotProvisionedByJpt(response: DescribeStacksResult): List<Stack> {
        return response
            .stacks
            .filter {
                it
                    .tags
                    .filter { it.key == Investment.lifespanKey }
                    .toList()
                    .isNotEmpty()
            }
    }
}