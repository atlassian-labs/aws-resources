package com.atlassian.performance.tools.aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.ResponseMetadata
import com.amazonaws.regions.Region
import com.amazonaws.services.support.AWSSupport
import com.amazonaws.services.support.model.*

/**
 * Open for overriding specific methods. No, Mockito is still not worth its magic.
 * Also, a great reminder to keep your interfaces lean.
 */
open class FakeSupport : AWSSupport {
    override fun describeServices(describeServicesRequest: DescribeServicesRequest?): DescribeServicesResult {
        throw Exception("unexpected call")
    }

    override fun describeServices(): DescribeServicesResult {
        throw Exception("unexpected call")
    }

    override fun describeTrustedAdvisorChecks(describeTrustedAdvisorChecksRequest: DescribeTrustedAdvisorChecksRequest?): DescribeTrustedAdvisorChecksResult {
        throw Exception("unexpected call")
    }

    override fun describeTrustedAdvisorCheckRefreshStatuses(describeTrustedAdvisorCheckRefreshStatusesRequest: DescribeTrustedAdvisorCheckRefreshStatusesRequest?): DescribeTrustedAdvisorCheckRefreshStatusesResult {
        throw Exception("unexpected call")
    }

    override fun describeTrustedAdvisorCheckResult(describeTrustedAdvisorCheckResultRequest: DescribeTrustedAdvisorCheckResultRequest?): DescribeTrustedAdvisorCheckResultResult {
        throw Exception("unexpected call")
    }

    override fun describeCases(describeCasesRequest: DescribeCasesRequest?): DescribeCasesResult {
        throw Exception("unexpected call")
    }

    override fun describeCases(): DescribeCasesResult {
        throw Exception("unexpected call")
    }

    override fun getCachedResponseMetadata(request: AmazonWebServiceRequest?): ResponseMetadata {
        throw Exception("unexpected call")
    }

    override fun describeAttachment(describeAttachmentRequest: DescribeAttachmentRequest?): DescribeAttachmentResult {
        throw Exception("unexpected call")
    }

    override fun setRegion(region: Region?) {
        throw Exception("unexpected call")
    }

    override fun resolveCase(resolveCaseRequest: ResolveCaseRequest?): ResolveCaseResult {
        throw Exception("unexpected call")
    }

    override fun resolveCase(): ResolveCaseResult {
        throw Exception("unexpected call")
    }

    override fun shutdown() {
        throw Exception("unexpected call")
    }

    override fun setEndpoint(endpoint: String?) {
        throw Exception("unexpected call")
    }

    override fun refreshTrustedAdvisorCheck(refreshTrustedAdvisorCheckRequest: RefreshTrustedAdvisorCheckRequest?): RefreshTrustedAdvisorCheckResult {
        throw Exception("unexpected call")
    }

    override fun createCase(createCaseRequest: CreateCaseRequest?): CreateCaseResult {
        throw Exception("unexpected call")
    }

    override fun describeSeverityLevels(describeSeverityLevelsRequest: DescribeSeverityLevelsRequest?): DescribeSeverityLevelsResult {
        throw Exception("unexpected call")
    }

    override fun describeSeverityLevels(): DescribeSeverityLevelsResult {
        throw Exception("unexpected call")
    }

    override fun describeCommunications(describeCommunicationsRequest: DescribeCommunicationsRequest?): DescribeCommunicationsResult {
        throw Exception("unexpected call")
    }

    override fun describeTrustedAdvisorCheckSummaries(describeTrustedAdvisorCheckSummariesRequest: DescribeTrustedAdvisorCheckSummariesRequest?): DescribeTrustedAdvisorCheckSummariesResult {
        throw Exception("unexpected call")
    }

    override fun addCommunicationToCase(addCommunicationToCaseRequest: AddCommunicationToCaseRequest?): AddCommunicationToCaseResult {
        throw Exception("unexpected call")
    }

    override fun addAttachmentsToSet(addAttachmentsToSetRequest: AddAttachmentsToSetRequest?): AddAttachmentsToSetResult {
        throw Exception("unexpected call")
    }
}