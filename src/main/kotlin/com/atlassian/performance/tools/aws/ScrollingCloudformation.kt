package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.Stack

/**
 * Scrolls through batches of AWS CloudFormation stacks.
 */
class ScrollingCloudformation(
    private val cloudformation: AmazonCloudFormation
) {
    fun scrollThroughStacks(
        batchAction: (List<Stack>) -> Unit
    ) {
        var token: String? = null
        do {
            val response = cloudformation.describeStacks(
                DescribeStacksRequest().withNextToken(token)
            )
            batchAction(response.stacks)
            token = response.nextToken
        } while (token != null)
    }
}