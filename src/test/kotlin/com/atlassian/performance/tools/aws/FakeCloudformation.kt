package com.atlassian.performance.tools.aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.ResponseMetadata
import com.amazonaws.regions.Region
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.*
import com.amazonaws.services.cloudformation.waiters.AmazonCloudFormationWaiters

class FakeCloudformation : AmazonCloudFormation {
    override fun updateStackInstances(updateStackInstancesRequest: UpdateStackInstancesRequest?): UpdateStackInstancesResult {
        throw Exception("unexpected call")
    }

    override fun updateTerminationProtection(updateTerminationProtectionRequest: UpdateTerminationProtectionRequest?): UpdateTerminationProtectionResult {
        throw Exception("unexpected call")
    }

    override fun listChangeSets(listChangeSetsRequest: ListChangeSetsRequest?): ListChangeSetsResult {
        throw Exception("unexpected call")
    }

    override fun stopStackSetOperation(stopStackSetOperationRequest: StopStackSetOperationRequest?): StopStackSetOperationResult {
        throw Exception("unexpected call")
    }

    override fun createStackSet(createStackSetRequest: CreateStackSetRequest?): CreateStackSetResult {
        throw Exception("unexpected call")
    }

    override fun estimateTemplateCost(estimateTemplateCostRequest: EstimateTemplateCostRequest?): EstimateTemplateCostResult {
        throw Exception("unexpected call")
    }

    override fun estimateTemplateCost(): EstimateTemplateCostResult {
        throw Exception("unexpected call")
    }

    override fun describeChangeSet(describeChangeSetRequest: DescribeChangeSetRequest?): DescribeChangeSetResult {
        throw Exception("unexpected call")
    }

    override fun describeStackResource(describeStackResourceRequest: DescribeStackResourceRequest?): DescribeStackResourceResult {
        throw Exception("unexpected call")
    }

    override fun deleteStackSet(deleteStackSetRequest: DeleteStackSetRequest?): DeleteStackSetResult {
        throw Exception("unexpected call")
    }

    override fun describeStacks(describeStacksRequest: DescribeStacksRequest?): DescribeStacksResult {
        throw Exception("unexpected call")
    }

    override fun describeStacks(): DescribeStacksResult {
        throw Exception("unexpected call")
    }

    override fun deleteStackInstances(deleteStackInstancesRequest: DeleteStackInstancesRequest?): DeleteStackInstancesResult {
        throw Exception("unexpected call")
    }

    override fun listStacks(listStacksRequest: ListStacksRequest?): ListStacksResult {
        throw Exception("unexpected call")
    }

    override fun listStacks(): ListStacksResult {
        throw Exception("unexpected call")
    }

    override fun updateStack(updateStackRequest: UpdateStackRequest?): UpdateStackResult {
        throw Exception("unexpected call")
    }

    override fun describeStackEvents(describeStackEventsRequest: DescribeStackEventsRequest?): DescribeStackEventsResult {
        throw Exception("unexpected call")
    }

    override fun describeStackInstance(describeStackInstanceRequest: DescribeStackInstanceRequest?): DescribeStackInstanceResult {
        throw Exception("unexpected call")
    }

    override fun describeStackSet(describeStackSetRequest: DescribeStackSetRequest?): DescribeStackSetResult {
        throw Exception("unexpected call")
    }

    override fun listExports(listExportsRequest: ListExportsRequest?): ListExportsResult {
        throw Exception("unexpected call")
    }

    override fun validateTemplate(validateTemplateRequest: ValidateTemplateRequest?): ValidateTemplateResult {
        throw Exception("unexpected call")
    }

    @Suppress("OverridingDeprecatedMember")
    override fun setEndpoint(endpoint: String?) {
        throw Exception("unexpected call")
    }

    override fun getStackPolicy(getStackPolicyRequest: GetStackPolicyRequest?): GetStackPolicyResult {
        throw Exception("unexpected call")
    }

    override fun createStack(createStackRequest: CreateStackRequest?): CreateStackResult {
        throw Exception("unexpected call")
    }

    override fun executeChangeSet(executeChangeSetRequest: ExecuteChangeSetRequest?): ExecuteChangeSetResult {
        throw Exception("unexpected call")
    }

    override fun describeStackSetOperation(describeStackSetOperationRequest: DescribeStackSetOperationRequest?): DescribeStackSetOperationResult {
        throw Exception("unexpected call")
    }

    override fun getTemplate(getTemplateRequest: GetTemplateRequest?): GetTemplateResult {
        throw Exception("unexpected call")
    }

    override fun signalResource(signalResourceRequest: SignalResourceRequest?): SignalResourceResult {
        throw Exception("unexpected call")
    }

    override fun listStackSetOperations(listStackSetOperationsRequest: ListStackSetOperationsRequest?): ListStackSetOperationsResult {
        throw Exception("unexpected call")
    }

    override fun listStackSets(listStackSetsRequest: ListStackSetsRequest?): ListStackSetsResult {
        throw Exception("unexpected call")
    }

    override fun updateStackSet(updateStackSetRequest: UpdateStackSetRequest?): UpdateStackSetResult {
        throw Exception("unexpected call")
    }

    override fun getTemplateSummary(getTemplateSummaryRequest: GetTemplateSummaryRequest?): GetTemplateSummaryResult {
        throw Exception("unexpected call")
    }

    override fun getTemplateSummary(): GetTemplateSummaryResult {
        throw Exception("unexpected call")
    }

    override fun listStackResources(listStackResourcesRequest: ListStackResourcesRequest?): ListStackResourcesResult {
        throw Exception("unexpected call")
    }

    override fun waiters(): AmazonCloudFormationWaiters {
        throw Exception("unexpected call")
    }

    override fun deleteStack(deleteStackRequest: DeleteStackRequest?): DeleteStackResult {
        throw Exception("unexpected call")
    }

    override fun listStackInstances(listStackInstancesRequest: ListStackInstancesRequest?): ListStackInstancesResult {
        throw Exception("unexpected call")
    }

    override fun createStackInstances(createStackInstancesRequest: CreateStackInstancesRequest?): CreateStackInstancesResult {
        throw Exception("unexpected call")
    }

    override fun getCachedResponseMetadata(request: AmazonWebServiceRequest?): ResponseMetadata {
        throw Exception("unexpected call")
    }

    override fun continueUpdateRollback(continueUpdateRollbackRequest: ContinueUpdateRollbackRequest?): ContinueUpdateRollbackResult {
        throw Exception("unexpected call")
    }

    override fun describeAccountLimits(describeAccountLimitsRequest: DescribeAccountLimitsRequest?): DescribeAccountLimitsResult {
        throw Exception("unexpected call")
    }

    @Suppress("OverridingDeprecatedMember")
    override fun setRegion(region: Region?) {
        throw Exception("unexpected call")
    }

    override fun listStackSetOperationResults(listStackSetOperationResultsRequest: ListStackSetOperationResultsRequest?): ListStackSetOperationResultsResult {
        throw Exception("unexpected call")
    }

    override fun shutdown() {
        throw Exception("unexpected call")
    }

    override fun cancelUpdateStack(cancelUpdateStackRequest: CancelUpdateStackRequest?): CancelUpdateStackResult {
        throw Exception("unexpected call")
    }

    override fun createChangeSet(createChangeSetRequest: CreateChangeSetRequest?): CreateChangeSetResult {
        throw Exception("unexpected call")
    }

    override fun setStackPolicy(setStackPolicyRequest: SetStackPolicyRequest?): SetStackPolicyResult {
        throw Exception("unexpected call")
    }

    override fun listImports(listImportsRequest: ListImportsRequest?): ListImportsResult {
        throw Exception("unexpected call")
    }

    override fun describeStackResources(describeStackResourcesRequest: DescribeStackResourcesRequest?): DescribeStackResourcesResult {
        throw Exception("unexpected call")
    }

    override fun deleteChangeSet(deleteChangeSetRequest: DeleteChangeSetRequest?): DeleteChangeSetResult {
        throw Exception("unexpected call")
    }
}