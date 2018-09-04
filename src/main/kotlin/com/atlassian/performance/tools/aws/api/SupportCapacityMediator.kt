package com.atlassian.performance.tools.aws.api

import com.amazonaws.regions.Regions
import com.amazonaws.services.support.AWSSupport
import com.amazonaws.services.support.model.CreateCaseRequest

/**
 * Requests limit increases via AWS Support.
 * Tries to avoid too many support cases.
 */
class SupportCapacityMediator(
    private val support: AWSSupport,
    private val region: Regions
) : CapacityMediator {

    override fun bump(
        limitType: String,
        desiredLimit: () -> Int
    ): String {
        val subject = "Raise service limit: $limitType"
        val openCase = support
            .describeCases()
            .cases
            .firstOrNull { it.subject == subject }
        return if (openCase != null) {
            "We recently opened a support case, which should resolve the problem:" +
                " ${openCase.caseId}: ${openCase.subject}." +
                " Please retry tomorrow and if the issue still persists, escalate this problem."
        } else {
            val limit = "$limitType to ${desiredLimit()}"
            createCase(prepareSupportRequest(subject, limit))
        }
    }

    private fun createCase(
        request: CreateCaseRequest
    ): String {
        val caseId = support.createCase(request).caseId
        return "Opened support case $caseId: ${request.subject}"
    }

    private fun prepareSupportRequest(
        subject: String,
        limit: String
    ): CreateCaseRequest = CreateCaseRequest()
        .withSubject(subject)
        .withIssueType("technical")
        .withServiceCode("amazon-elastic-compute-cloud-linux")
        .withCategoryCode("general-guidance")
        .withSeverityCode("normal")
        .withLanguage("en")
        .withCommunicationBody(
            """
            |Hi,
            |
            |Please raise the ${region.getName()} region $limit.
            |
            |We sometimes migrate between accounts, regions, instance types
            |and we need the additional capacity to perform our performance tests.
            |
            |Thanks,
            |Maciej
            """.trimMargin()
        )
}