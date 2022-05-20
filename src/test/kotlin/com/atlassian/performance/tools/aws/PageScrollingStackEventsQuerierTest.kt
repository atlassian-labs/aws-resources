package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.*
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class PageScrollingStackEventsQuerierTest {

    @Test
    fun worksWithSinglePage() {
        val cloudformation = CloudFormationMock(
            resultSequence = buildPageSequence(
                numberOfPages = 1,
                numberOfEventsPerPage = 3,
                eventBuilder = { pageNumber, eventNumber ->
                    StackEvent().apply { eventId = "$pageNumber-$eventNumber" }
                }
            )
        )
        val querier = PageScrollingStackEventsQuerier.Builder(cloudformation).build()

        val events = querier.getEvents("stackId")

        val requests = cloudformation.getRequests()
        assertThat(requests, hasSize(1))
        assertThat(events, hasSize(3))
    }

    @Test
    fun joinsResultsFromTwoRequests() {
        val cloudformation = CloudFormationMock(
            resultSequence = buildPageSequence(
                numberOfPages = 2,
                numberOfEventsPerPage = 2,
                eventBuilder = { pageNumber, eventNumber ->
                    StackEvent().apply { eventId = "$pageNumber-$eventNumber" }
                }
            )
        )
        val querier = PageScrollingStackEventsQuerier.Builder(cloudformation).build()

        val events = querier.getEvents("stackId")

        val requests = cloudformation.getRequests()
        assertThat(events, hasSize(4))
        assertThat(requests, hasSize(2))
    }

    @Test
    fun hasDefaultLimitOf20Requests() {
        val cloudformation = CloudFormationMock(
            resultSequence = buildPageSequence(
                numberOfPages = 21,
                numberOfEventsPerPage = 3,
                eventBuilder = { pageNumber, eventNumber ->
                    StackEvent().apply { eventId = "$pageNumber-$eventNumber" }
                }
            )
        )
        val querier = PageScrollingStackEventsQuerier.Builder(cloudformation).build()

        val events = querier.getEvents("stackId")

        val requests = cloudformation.getRequests()
        assertThat(events, hasSize(60))
        assertThat(requests, hasSize(20))
    }

    @Test
    fun customNextPagePredicateCanBeApplied() {
        val cloudformation = CloudFormationMock(
            resultSequence = buildPageSequence(
                numberOfPages = 100,
                numberOfEventsPerPage = 10,
                eventBuilder = { pageNumber, eventNumber ->
                    StackEvent().apply { eventId = "$pageNumber-$eventNumber" }
                }
            )
        )
        val querier = PageScrollingStackEventsQuerier.Builder(cloudformation)
            .nextPageScrollPredicate { it.stackEvents.none { event -> event.eventId == "29-1" } }
            .build()

        val events = querier.getEvents("stackId")

        val requests = cloudformation.getRequests()
        assertThat(events, hasSize(290))
        assertThat(requests, hasSize(29))
    }

    @Test(expected = AmazonCloudFormationException::class)
    fun awsExceptionPassesThough() {
        val cloudformation = CloudFormationMock(
            resultSequence = sequenceOf( { throw AmazonCloudFormationException("AWS exception") } )
        )
        val querier = PageScrollingStackEventsQuerier.Builder(cloudformation).build()

        querier.getEvents("stackId")
    }

    private fun buildPageSequence(
        numberOfPages: Int,
        numberOfEventsPerPage: Int,
        eventBuilder: (pageNumber: Int, eventNumber: Int) -> StackEvent
    ) = (1..numberOfPages).map { pageNumber ->
        DescribeStackEventsResult().apply {
            setStackEvents((1..numberOfEventsPerPage).map { eventNumber -> eventBuilder(pageNumber, eventNumber) })
            if (pageNumber < numberOfPages) {
                nextToken = "${pageNumber + 1}"
            }
        }
    }.map { { it } }.asSequence()

    private class CloudFormationMock(
        resultSequence: Sequence<() -> DescribeStackEventsResult>
    ) : AmazonCloudFormation by FakeCloudformation() {
        private val resultsIterator = resultSequence.iterator()
        private val requests = mutableListOf<DescribeStackEventsRequest?>()

        override fun describeStackEvents(
            describeStackEventsRequest: DescribeStackEventsRequest?
        ) = resultsIterator.next()
            .also { requests.add(describeStackEventsRequest) }
            .invoke()

        fun getRequests() = requests.toList()
    }
}