package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStackEventsRequest
import com.amazonaws.services.cloudformation.model.DescribeStackEventsResult

internal class PageScrollingStackEventsQuerier private constructor(
    private val cloudformation: AmazonCloudFormation,
    private val nextPageScrollPredicate: (DescribeStackEventsResult) -> Boolean
) : StackEventsProvider {
    override fun getEvents(
        stackId: String
    ) = generateSequence(
        seedFunction = {
            cloudformation.describeStackEvents(
                DescribeStackEventsRequest()
                    .withStackName(stackId)
            )
        },
        nextFunction = { previousResult ->
            previousResult.takeIf(nextPageScrollPredicate)?.nextToken?.let { nextToken ->
                cloudformation.describeStackEvents(
                    DescribeStackEventsRequest()
                        .withStackName(stackId)
                        .withNextToken(nextToken)
                )
            }
        }
    ).toList().flatMap { it.stackEvents }

    internal class Builder(
        private val cloudformation: AmazonCloudFormation
    ) {
        private var nextPageScrollPredicate: (DescribeStackEventsResult) -> Boolean = CountingPredicate { counter, _ -> counter < 20 }

        fun nextPageScrollPredicate(
            nextPageScrollPredicate: (DescribeStackEventsResult) -> Boolean
        ) = apply { this.nextPageScrollPredicate = nextPageScrollPredicate }

        fun build() = PageScrollingStackEventsQuerier(
            cloudformation = cloudformation,
            nextPageScrollPredicate = nextPageScrollPredicate
        )
    }
}