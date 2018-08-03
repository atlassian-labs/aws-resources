package com.atlassian.performance.tools.aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.ResponseMetadata
import com.amazonaws.regions.Region
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.*
import com.amazonaws.services.ec2.waiters.AmazonEC2Waiters

/**
 * Open for overriding specific methods. No, Mockito is still not worth its magic.
 * Also, a great reminder to keep your interfaces lean.
 */
open class FakeEc2 : AmazonEC2 {
    override fun enableVpcClassicLink(enableVpcClassicLinkRequest: EnableVpcClassicLinkRequest?): EnableVpcClassicLinkResult {
        throw Exception("unexpected call")
    }

    override fun createNetworkAcl(createNetworkAclRequest: CreateNetworkAclRequest?): CreateNetworkAclResult {
        throw Exception("unexpected call")
    }

    override fun createVpnConnection(createVpnConnectionRequest: CreateVpnConnectionRequest?): CreateVpnConnectionResult {
        throw Exception("unexpected call")
    }

    override fun associateVpcCidrBlock(associateVpcCidrBlockRequest: AssociateVpcCidrBlockRequest?): AssociateVpcCidrBlockResult {
        throw Exception("unexpected call")
    }

    override fun describeLaunchTemplateVersions(describeLaunchTemplateVersionsRequest: DescribeLaunchTemplateVersionsRequest?): DescribeLaunchTemplateVersionsResult {
        throw Exception("unexpected call")
    }

    override fun updateSecurityGroupRuleDescriptionsIngress(updateSecurityGroupRuleDescriptionsIngressRequest: UpdateSecurityGroupRuleDescriptionsIngressRequest?): UpdateSecurityGroupRuleDescriptionsIngressResult {
        throw Exception("unexpected call")
    }

    override fun createNetworkInterface(createNetworkInterfaceRequest: CreateNetworkInterfaceRequest?): CreateNetworkInterfaceResult {
        throw Exception("unexpected call")
    }

    override fun deleteFlowLogs(deleteFlowLogsRequest: DeleteFlowLogsRequest?): DeleteFlowLogsResult {
        throw Exception("unexpected call")
    }

    override fun createVolume(createVolumeRequest: CreateVolumeRequest?): CreateVolumeResult {
        throw Exception("unexpected call")
    }

    override fun setEndpoint(endpoint: String?) {
        throw Exception("unexpected call")
    }

    override fun describeAddresses(describeAddressesRequest: DescribeAddressesRequest?): DescribeAddressesResult {
        throw Exception("unexpected call")
    }

    override fun describeAddresses(): DescribeAddressesResult {
        throw Exception("unexpected call")
    }

    override fun resetImageAttribute(resetImageAttributeRequest: ResetImageAttributeRequest?): ResetImageAttributeResult {
        throw Exception("unexpected call")
    }

    override fun modifySnapshotAttribute(modifySnapshotAttributeRequest: ModifySnapshotAttributeRequest?): ModifySnapshotAttributeResult {
        throw Exception("unexpected call")
    }

    override fun importSnapshot(importSnapshotRequest: ImportSnapshotRequest?): ImportSnapshotResult {
        throw Exception("unexpected call")
    }

    override fun importSnapshot(): ImportSnapshotResult {
        throw Exception("unexpected call")
    }

    override fun modifyInstancePlacement(modifyInstancePlacementRequest: ModifyInstancePlacementRequest?): ModifyInstancePlacementResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcs(describeVpcsRequest: DescribeVpcsRequest?): DescribeVpcsResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcs(): DescribeVpcsResult {
        throw Exception("unexpected call")
    }

    override fun attachInternetGateway(attachInternetGatewayRequest: AttachInternetGatewayRequest?): AttachInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun modifyInstanceCreditSpecification(modifyInstanceCreditSpecificationRequest: ModifyInstanceCreditSpecificationRequest?): ModifyInstanceCreditSpecificationResult {
        throw Exception("unexpected call")
    }

    override fun createReservedInstancesListing(createReservedInstancesListingRequest: CreateReservedInstancesListingRequest?): CreateReservedInstancesListingResult {
        throw Exception("unexpected call")
    }

    override fun createSnapshot(createSnapshotRequest: CreateSnapshotRequest?): CreateSnapshotResult {
        throw Exception("unexpected call")
    }

    override fun describeNetworkInterfacePermissions(describeNetworkInterfacePermissionsRequest: DescribeNetworkInterfacePermissionsRequest?): DescribeNetworkInterfacePermissionsResult {
        throw Exception("unexpected call")
    }

    override fun modifyImageAttribute(modifyImageAttributeRequest: ModifyImageAttributeRequest?): ModifyImageAttributeResult {
        throw Exception("unexpected call")
    }

    override fun purchaseHostReservation(purchaseHostReservationRequest: PurchaseHostReservationRequest?): PurchaseHostReservationResult {
        throw Exception("unexpected call")
    }

    override fun createImage(createImageRequest: CreateImageRequest?): CreateImageResult {
        throw Exception("unexpected call")
    }

    override fun createFlowLogs(createFlowLogsRequest: CreateFlowLogsRequest?): CreateFlowLogsResult {
        throw Exception("unexpected call")
    }

    override fun describeVolumes(describeVolumesRequest: DescribeVolumesRequest?): DescribeVolumesResult {
        throw Exception("unexpected call")
    }

    override fun describeVolumes(): DescribeVolumesResult {
        throw Exception("unexpected call")
    }

    override fun describePrincipalIdFormat(describePrincipalIdFormatRequest: DescribePrincipalIdFormatRequest?): DescribePrincipalIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun describeVpnGateways(describeVpnGatewaysRequest: DescribeVpnGatewaysRequest?): DescribeVpnGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeVpnGateways(): DescribeVpnGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotFleetRequests(describeSpotFleetRequestsRequest: DescribeSpotFleetRequestsRequest?): DescribeSpotFleetRequestsResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotFleetRequests(): DescribeSpotFleetRequestsResult {
        throw Exception("unexpected call")
    }

    override fun revokeSecurityGroupIngress(revokeSecurityGroupIngressRequest: RevokeSecurityGroupIngressRequest?): RevokeSecurityGroupIngressResult {
        throw Exception("unexpected call")
    }

    override fun revokeSecurityGroupIngress(): RevokeSecurityGroupIngressResult {
        throw Exception("unexpected call")
    }

    override fun getPasswordData(getPasswordDataRequest: GetPasswordDataRequest?): GetPasswordDataResult {
        throw Exception("unexpected call")
    }

    override fun describeKeyPairs(describeKeyPairsRequest: DescribeKeyPairsRequest?): DescribeKeyPairsResult {
        throw Exception("unexpected call")
    }

    override fun describeKeyPairs(): DescribeKeyPairsResult {
        throw Exception("unexpected call")
    }

    override fun terminateInstances(terminateInstancesRequest: TerminateInstancesRequest?): TerminateInstancesResult {
        throw Exception("unexpected call")
    }

    override fun createCustomerGateway(createCustomerGatewayRequest: CreateCustomerGatewayRequest?): CreateCustomerGatewayResult {
        throw Exception("unexpected call")
    }

    override fun modifyVolumeAttribute(modifyVolumeAttributeRequest: ModifyVolumeAttributeRequest?): ModifyVolumeAttributeResult {
        throw Exception("unexpected call")
    }

    override fun startInstances(startInstancesRequest: StartInstancesRequest?): StartInstancesResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpnConnectionRoute(deleteVpnConnectionRouteRequest: DeleteVpnConnectionRouteRequest?): DeleteVpnConnectionRouteResult {
        throw Exception("unexpected call")
    }

    override fun describeImageAttribute(describeImageAttributeRequest: DescribeImageAttributeRequest?): DescribeImageAttributeResult {
        throw Exception("unexpected call")
    }

    override fun describeRouteTables(describeRouteTablesRequest: DescribeRouteTablesRequest?): DescribeRouteTablesResult {
        throw Exception("unexpected call")
    }

    override fun describeRouteTables(): DescribeRouteTablesResult {
        throw Exception("unexpected call")
    }

    override fun describeIamInstanceProfileAssociations(describeIamInstanceProfileAssociationsRequest: DescribeIamInstanceProfileAssociationsRequest?): DescribeIamInstanceProfileAssociationsResult {
        throw Exception("unexpected call")
    }

    override fun modifyInstanceAttribute(modifyInstanceAttributeRequest: ModifyInstanceAttributeRequest?): ModifyInstanceAttributeResult {
        throw Exception("unexpected call")
    }

    override fun createVpcPeeringConnection(createVpcPeeringConnectionRequest: CreateVpcPeeringConnectionRequest?): CreateVpcPeeringConnectionResult {
        throw Exception("unexpected call")
    }

    override fun createVpcPeeringConnection(): CreateVpcPeeringConnectionResult {
        throw Exception("unexpected call")
    }

    override fun describeAvailabilityZones(describeAvailabilityZonesRequest: DescribeAvailabilityZonesRequest?): DescribeAvailabilityZonesResult {
        throw Exception("unexpected call")
    }

    override fun describeAvailabilityZones(): DescribeAvailabilityZonesResult {
        throw Exception("unexpected call")
    }

    override fun stopInstances(stopInstancesRequest: StopInstancesRequest?): StopInstancesResult {
        throw Exception("unexpected call")
    }

    override fun describeHostReservationOfferings(describeHostReservationOfferingsRequest: DescribeHostReservationOfferingsRequest?): DescribeHostReservationOfferingsResult {
        throw Exception("unexpected call")
    }

    override fun describeSnapshots(describeSnapshotsRequest: DescribeSnapshotsRequest?): DescribeSnapshotsResult {
        throw Exception("unexpected call")
    }

    override fun describeSnapshots(): DescribeSnapshotsResult {
        throw Exception("unexpected call")
    }

    override fun deleteLaunchTemplateVersions(deleteLaunchTemplateVersionsRequest: DeleteLaunchTemplateVersionsRequest?): DeleteLaunchTemplateVersionsResult {
        throw Exception("unexpected call")
    }

    override fun describeDhcpOptions(describeDhcpOptionsRequest: DescribeDhcpOptionsRequest?): DescribeDhcpOptionsResult {
        throw Exception("unexpected call")
    }

    override fun describeDhcpOptions(): DescribeDhcpOptionsResult {
        throw Exception("unexpected call")
    }

    override fun attachVolume(attachVolumeRequest: AttachVolumeRequest?): AttachVolumeResult {
        throw Exception("unexpected call")
    }

    override fun getConsoleScreenshot(getConsoleScreenshotRequest: GetConsoleScreenshotRequest?): GetConsoleScreenshotResult {
        throw Exception("unexpected call")
    }

    override fun describePlacementGroups(describePlacementGroupsRequest: DescribePlacementGroupsRequest?): DescribePlacementGroupsResult {
        throw Exception("unexpected call")
    }

    override fun describePlacementGroups(): DescribePlacementGroupsResult {
        throw Exception("unexpected call")
    }

    override fun disassociateSubnetCidrBlock(disassociateSubnetCidrBlockRequest: DisassociateSubnetCidrBlockRequest?): DisassociateSubnetCidrBlockResult {
        throw Exception("unexpected call")
    }

    override fun deleteTags(deleteTagsRequest: DeleteTagsRequest?): DeleteTagsResult {
        throw Exception("unexpected call")
    }

    override fun getReservedInstancesExchangeQuote(getReservedInstancesExchangeQuoteRequest: GetReservedInstancesExchangeQuoteRequest?): GetReservedInstancesExchangeQuoteResult {
        throw Exception("unexpected call")
    }

    override fun cancelImportTask(cancelImportTaskRequest: CancelImportTaskRequest?): CancelImportTaskResult {
        throw Exception("unexpected call")
    }

    override fun cancelImportTask(): CancelImportTaskResult {
        throw Exception("unexpected call")
    }

    override fun <X : AmazonWebServiceRequest?> dryRun(request: DryRunSupportedRequest<X>?): DryRunResult<X> {
        throw Exception("unexpected call")
    }

    override fun createDefaultVpc(createDefaultVpcRequest: CreateDefaultVpcRequest?): CreateDefaultVpcResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotFleetInstances(describeSpotFleetInstancesRequest: DescribeSpotFleetInstancesRequest?): DescribeSpotFleetInstancesResult {
        throw Exception("unexpected call")
    }

    override fun importImage(importImageRequest: ImportImageRequest?): ImportImageResult {
        throw Exception("unexpected call")
    }

    override fun importImage(): ImportImageResult {
        throw Exception("unexpected call")
    }

    override fun resetFpgaImageAttribute(resetFpgaImageAttributeRequest: ResetFpgaImageAttributeRequest?): ResetFpgaImageAttributeResult {
        throw Exception("unexpected call")
    }

    override fun createNetworkAclEntry(createNetworkAclEntryRequest: CreateNetworkAclEntryRequest?): CreateNetworkAclEntryResult {
        throw Exception("unexpected call")
    }

    override fun getCachedResponseMetadata(request: AmazonWebServiceRequest?): ResponseMetadata {
        throw Exception("unexpected call")
    }

    override fun describeCustomerGateways(describeCustomerGatewaysRequest: DescribeCustomerGatewaysRequest?): DescribeCustomerGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeCustomerGateways(): DescribeCustomerGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeHostReservations(describeHostReservationsRequest: DescribeHostReservationsRequest?): DescribeHostReservationsResult {
        throw Exception("unexpected call")
    }

    override fun deleteSubnet(deleteSubnetRequest: DeleteSubnetRequest?): DeleteSubnetResult {
        throw Exception("unexpected call")
    }

    override fun describeVolumeStatus(describeVolumeStatusRequest: DescribeVolumeStatusRequest?): DescribeVolumeStatusResult {
        throw Exception("unexpected call")
    }

    override fun describeVolumeStatus(): DescribeVolumeStatusResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotInstanceRequests(describeSpotInstanceRequestsRequest: DescribeSpotInstanceRequestsRequest?): DescribeSpotInstanceRequestsResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotInstanceRequests(): DescribeSpotInstanceRequestsResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpointServiceConfigurations(describeVpcEndpointServiceConfigurationsRequest: DescribeVpcEndpointServiceConfigurationsRequest?): DescribeVpcEndpointServiceConfigurationsResult {
        throw Exception("unexpected call")
    }

    override fun deleteFpgaImage(deleteFpgaImageRequest: DeleteFpgaImageRequest?): DeleteFpgaImageResult {
        throw Exception("unexpected call")
    }

    override fun deleteRouteTable(deleteRouteTableRequest: DeleteRouteTableRequest?): DeleteRouteTableResult {
        throw Exception("unexpected call")
    }

    override fun replaceIamInstanceProfileAssociation(replaceIamInstanceProfileAssociationRequest: ReplaceIamInstanceProfileAssociationRequest?): ReplaceIamInstanceProfileAssociationResult {
        throw Exception("unexpected call")
    }

    override fun describeTags(describeTagsRequest: DescribeTagsRequest?): DescribeTagsResult {
        throw Exception("unexpected call")
    }

    override fun describeTags(): DescribeTagsResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcAttribute(modifyVpcAttributeRequest: ModifyVpcAttributeRequest?): ModifyVpcAttributeResult {
        throw Exception("unexpected call")
    }

    override fun describeFpgaImageAttribute(describeFpgaImageAttributeRequest: DescribeFpgaImageAttributeRequest?): DescribeFpgaImageAttributeResult {
        throw Exception("unexpected call")
    }

    override fun describeClassicLinkInstances(describeClassicLinkInstancesRequest: DescribeClassicLinkInstancesRequest?): DescribeClassicLinkInstancesResult {
        throw Exception("unexpected call")
    }

    override fun describeClassicLinkInstances(): DescribeClassicLinkInstancesResult {
        throw Exception("unexpected call")
    }

    override fun releaseHosts(releaseHostsRequest: ReleaseHostsRequest?): ReleaseHostsResult {
        throw Exception("unexpected call")
    }

    override fun describeNatGateways(describeNatGatewaysRequest: DescribeNatGatewaysRequest?): DescribeNatGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcClassicLink(describeVpcClassicLinkRequest: DescribeVpcClassicLinkRequest?): DescribeVpcClassicLinkResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcClassicLink(): DescribeVpcClassicLinkResult {
        throw Exception("unexpected call")
    }

    override fun deleteVolume(deleteVolumeRequest: DeleteVolumeRequest?): DeleteVolumeResult {
        throw Exception("unexpected call")
    }

    override fun modifyFpgaImageAttribute(modifyFpgaImageAttributeRequest: ModifyFpgaImageAttributeRequest?): ModifyFpgaImageAttributeResult {
        throw Exception("unexpected call")
    }

    override fun describeElasticGpus(describeElasticGpusRequest: DescribeElasticGpusRequest?): DescribeElasticGpusResult {
        throw Exception("unexpected call")
    }

    override fun resetNetworkInterfaceAttribute(resetNetworkInterfaceAttributeRequest: ResetNetworkInterfaceAttributeRequest?): ResetNetworkInterfaceAttributeResult {
        throw Exception("unexpected call")
    }

    override fun deleteNatGateway(deleteNatGatewayRequest: DeleteNatGatewayRequest?): DeleteNatGatewayResult {
        throw Exception("unexpected call")
    }

    override fun describeFpgaImages(describeFpgaImagesRequest: DescribeFpgaImagesRequest?): DescribeFpgaImagesResult {
        throw Exception("unexpected call")
    }

    override fun describeIdFormat(describeIdFormatRequest: DescribeIdFormatRequest?): DescribeIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun describeIdFormat(): DescribeIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcClassicLinkDnsSupport(describeVpcClassicLinkDnsSupportRequest: DescribeVpcClassicLinkDnsSupportRequest?): DescribeVpcClassicLinkDnsSupportResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcPeeringConnectionOptions(modifyVpcPeeringConnectionOptionsRequest: ModifyVpcPeeringConnectionOptionsRequest?): ModifyVpcPeeringConnectionOptionsResult {
        throw Exception("unexpected call")
    }

    override fun associateDhcpOptions(associateDhcpOptionsRequest: AssociateDhcpOptionsRequest?): AssociateDhcpOptionsResult {
        throw Exception("unexpected call")
    }

    override fun authorizeSecurityGroupEgress(authorizeSecurityGroupEgressRequest: AuthorizeSecurityGroupEgressRequest?): AuthorizeSecurityGroupEgressResult {
        throw Exception("unexpected call")
    }

    override fun describeInstanceAttribute(describeInstanceAttributeRequest: DescribeInstanceAttributeRequest?): DescribeInstanceAttributeResult {
        throw Exception("unexpected call")
    }

    override fun describeStaleSecurityGroups(describeStaleSecurityGroupsRequest: DescribeStaleSecurityGroupsRequest?): DescribeStaleSecurityGroupsResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcEndpointConnectionNotification(modifyVpcEndpointConnectionNotificationRequest: ModifyVpcEndpointConnectionNotificationRequest?): ModifyVpcEndpointConnectionNotificationResult {
        throw Exception("unexpected call")
    }

    override fun associateRouteTable(associateRouteTableRequest: AssociateRouteTableRequest?): AssociateRouteTableResult {
        throw Exception("unexpected call")
    }

    override fun monitorInstances(monitorInstancesRequest: MonitorInstancesRequest?): MonitorInstancesResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpcEndpointServiceConfigurations(deleteVpcEndpointServiceConfigurationsRequest: DeleteVpcEndpointServiceConfigurationsRequest?): DeleteVpcEndpointServiceConfigurationsResult {
        throw Exception("unexpected call")
    }

    override fun describeScheduledInstances(describeScheduledInstancesRequest: DescribeScheduledInstancesRequest?): DescribeScheduledInstancesResult {
        throw Exception("unexpected call")
    }

    override fun createTags(createTagsRequest: CreateTagsRequest?): CreateTagsResult {
        throw Exception("unexpected call")
    }

    override fun describeLaunchTemplates(describeLaunchTemplatesRequest: DescribeLaunchTemplatesRequest?): DescribeLaunchTemplatesResult {
        throw Exception("unexpected call")
    }

    override fun copyFpgaImage(copyFpgaImageRequest: CopyFpgaImageRequest?): CopyFpgaImageResult {
        throw Exception("unexpected call")
    }

    override fun enableVgwRoutePropagation(enableVgwRoutePropagationRequest: EnableVgwRoutePropagationRequest?): EnableVgwRoutePropagationResult {
        throw Exception("unexpected call")
    }

    override fun createVpcEndpoint(createVpcEndpointRequest: CreateVpcEndpointRequest?): CreateVpcEndpointResult {
        throw Exception("unexpected call")
    }

    override fun unassignIpv6Addresses(unassignIpv6AddressesRequest: UnassignIpv6AddressesRequest?): UnassignIpv6AddressesResult {
        throw Exception("unexpected call")
    }

    override fun associateSubnetCidrBlock(associateSubnetCidrBlockRequest: AssociateSubnetCidrBlockRequest?): AssociateSubnetCidrBlockResult {
        throw Exception("unexpected call")
    }

    override fun createVpnConnectionRoute(createVpnConnectionRouteRequest: CreateVpnConnectionRouteRequest?): CreateVpnConnectionRouteResult {
        throw Exception("unexpected call")
    }

    override fun replaceNetworkAclEntry(replaceNetworkAclEntryRequest: ReplaceNetworkAclEntryRequest?): ReplaceNetworkAclEntryResult {
        throw Exception("unexpected call")
    }

    override fun describeSnapshotAttribute(describeSnapshotAttributeRequest: DescribeSnapshotAttributeRequest?): DescribeSnapshotAttributeResult {
        throw Exception("unexpected call")
    }

    override fun describeVolumesModifications(describeVolumesModificationsRequest: DescribeVolumesModificationsRequest?): DescribeVolumesModificationsResult {
        throw Exception("unexpected call")
    }

    override fun modifyHosts(modifyHostsRequest: ModifyHostsRequest?): ModifyHostsResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotDatafeedSubscription(describeSpotDatafeedSubscriptionRequest: DescribeSpotDatafeedSubscriptionRequest?): DescribeSpotDatafeedSubscriptionResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotDatafeedSubscription(): DescribeSpotDatafeedSubscriptionResult {
        throw Exception("unexpected call")
    }

    override fun modifyLaunchTemplate(modifyLaunchTemplateRequest: ModifyLaunchTemplateRequest?): ModifyLaunchTemplateResult {
        throw Exception("unexpected call")
    }

    override fun deleteKeyPair(deleteKeyPairRequest: DeleteKeyPairRequest?): DeleteKeyPairResult {
        throw Exception("unexpected call")
    }

    override fun modifySpotFleetRequest(modifySpotFleetRequestRequest: ModifySpotFleetRequestRequest?): ModifySpotFleetRequestResult {
        throw Exception("unexpected call")
    }

    override fun rebootInstances(rebootInstancesRequest: RebootInstancesRequest?): RebootInstancesResult {
        throw Exception("unexpected call")
    }

    override fun modifySubnetAttribute(modifySubnetAttributeRequest: ModifySubnetAttributeRequest?): ModifySubnetAttributeResult {
        throw Exception("unexpected call")
    }

    override fun revokeSecurityGroupEgress(revokeSecurityGroupEgressRequest: RevokeSecurityGroupEgressRequest?): RevokeSecurityGroupEgressResult {
        throw Exception("unexpected call")
    }

    override fun describeInternetGateways(describeInternetGatewaysRequest: DescribeInternetGatewaysRequest?): DescribeInternetGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeInternetGateways(): DescribeInternetGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun createVpc(createVpcRequest: CreateVpcRequest?): CreateVpcResult {
        throw Exception("unexpected call")
    }

    override fun describeImages(describeImagesRequest: DescribeImagesRequest?): DescribeImagesResult {
        throw Exception("unexpected call")
    }

    override fun describeImages(): DescribeImagesResult {
        throw Exception("unexpected call")
    }

    override fun describeImportSnapshotTasks(describeImportSnapshotTasksRequest: DescribeImportSnapshotTasksRequest?): DescribeImportSnapshotTasksResult {
        throw Exception("unexpected call")
    }

    override fun describeImportSnapshotTasks(): DescribeImportSnapshotTasksResult {
        throw Exception("unexpected call")
    }

    override fun createVpnGateway(createVpnGatewayRequest: CreateVpnGatewayRequest?): CreateVpnGatewayResult {
        throw Exception("unexpected call")
    }

    override fun describeHosts(describeHostsRequest: DescribeHostsRequest?): DescribeHostsResult {
        throw Exception("unexpected call")
    }

    override fun describeHosts(): DescribeHostsResult {
        throw Exception("unexpected call")
    }

    override fun disassociateRouteTable(disassociateRouteTableRequest: DisassociateRouteTableRequest?): DisassociateRouteTableResult {
        throw Exception("unexpected call")
    }

    override fun requestSpotFleet(requestSpotFleetRequest: RequestSpotFleetRequest?): RequestSpotFleetResult {
        throw Exception("unexpected call")
    }

    override fun runScheduledInstances(runScheduledInstancesRequest: RunScheduledInstancesRequest?): RunScheduledInstancesResult {
        throw Exception("unexpected call")
    }

    override fun deleteSpotDatafeedSubscription(deleteSpotDatafeedSubscriptionRequest: DeleteSpotDatafeedSubscriptionRequest?): DeleteSpotDatafeedSubscriptionResult {
        throw Exception("unexpected call")
    }

    override fun deleteSpotDatafeedSubscription(): DeleteSpotDatafeedSubscriptionResult {
        throw Exception("unexpected call")
    }

    override fun deleteRoute(deleteRouteRequest: DeleteRouteRequest?): DeleteRouteResult {
        throw Exception("unexpected call")
    }

    override fun createInstanceExportTask(createInstanceExportTaskRequest: CreateInstanceExportTaskRequest?): CreateInstanceExportTaskResult {
        throw Exception("unexpected call")
    }

    override fun deleteLaunchTemplate(deleteLaunchTemplateRequest: DeleteLaunchTemplateRequest?): DeleteLaunchTemplateResult {
        throw Exception("unexpected call")
    }

    override fun describeSecurityGroupReferences(describeSecurityGroupReferencesRequest: DescribeSecurityGroupReferencesRequest?): DescribeSecurityGroupReferencesResult {
        throw Exception("unexpected call")
    }

    override fun purchaseReservedInstancesOffering(purchaseReservedInstancesOfferingRequest: PurchaseReservedInstancesOfferingRequest?): PurchaseReservedInstancesOfferingResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpcPeeringConnection(deleteVpcPeeringConnectionRequest: DeleteVpcPeeringConnectionRequest?): DeleteVpcPeeringConnectionResult {
        throw Exception("unexpected call")
    }

    override fun createPlacementGroup(createPlacementGroupRequest: CreatePlacementGroupRequest?): CreatePlacementGroupResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstancesModifications(describeReservedInstancesModificationsRequest: DescribeReservedInstancesModificationsRequest?): DescribeReservedInstancesModificationsResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstancesModifications(): DescribeReservedInstancesModificationsResult {
        throw Exception("unexpected call")
    }

    override fun waiters(): AmazonEC2Waiters {
        throw Exception("unexpected call")
    }

    override fun rejectVpcEndpointConnections(rejectVpcEndpointConnectionsRequest: RejectVpcEndpointConnectionsRequest?): RejectVpcEndpointConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun unassignPrivateIpAddresses(unassignPrivateIpAddressesRequest: UnassignPrivateIpAddressesRequest?): UnassignPrivateIpAddressesResult {
        throw Exception("unexpected call")
    }

    override fun modifyVolume(modifyVolumeRequest: ModifyVolumeRequest?): ModifyVolumeResult {
        throw Exception("unexpected call")
    }

    override fun unmonitorInstances(unmonitorInstancesRequest: UnmonitorInstancesRequest?): UnmonitorInstancesResult {
        throw Exception("unexpected call")
    }

    override fun deleteNetworkAclEntry(deleteNetworkAclEntryRequest: DeleteNetworkAclEntryRequest?): DeleteNetworkAclEntryResult {
        throw Exception("unexpected call")
    }

    override fun deleteSecurityGroup(deleteSecurityGroupRequest: DeleteSecurityGroupRequest?): DeleteSecurityGroupResult {
        throw Exception("unexpected call")
    }

    override fun replaceRouteTableAssociation(replaceRouteTableAssociationRequest: ReplaceRouteTableAssociationRequest?): ReplaceRouteTableAssociationResult {
        throw Exception("unexpected call")
    }

    override fun confirmProductInstance(confirmProductInstanceRequest: ConfirmProductInstanceRequest?): ConfirmProductInstanceResult {
        throw Exception("unexpected call")
    }

    override fun attachNetworkInterface(attachNetworkInterfaceRequest: AttachNetworkInterfaceRequest?): AttachNetworkInterfaceResult {
        throw Exception("unexpected call")
    }

    override fun describeNetworkAcls(describeNetworkAclsRequest: DescribeNetworkAclsRequest?): DescribeNetworkAclsResult {
        throw Exception("unexpected call")
    }

    override fun describeNetworkAcls(): DescribeNetworkAclsResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstances(describeReservedInstancesRequest: DescribeReservedInstancesRequest?): DescribeReservedInstancesResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstances(): DescribeReservedInstancesResult {
        throw Exception("unexpected call")
    }

    override fun describeNetworkInterfaceAttribute(describeNetworkInterfaceAttributeRequest: DescribeNetworkInterfaceAttributeRequest?): DescribeNetworkInterfaceAttributeResult {
        throw Exception("unexpected call")
    }

    override fun cancelConversionTask(cancelConversionTaskRequest: CancelConversionTaskRequest?): CancelConversionTaskResult {
        throw Exception("unexpected call")
    }

    override fun replaceNetworkAclAssociation(replaceNetworkAclAssociationRequest: ReplaceNetworkAclAssociationRequest?): ReplaceNetworkAclAssociationResult {
        throw Exception("unexpected call")
    }

    override fun moveAddressToVpc(moveAddressToVpcRequest: MoveAddressToVpcRequest?): MoveAddressToVpcResult {
        throw Exception("unexpected call")
    }

    override fun cancelBundleTask(cancelBundleTaskRequest: CancelBundleTaskRequest?): CancelBundleTaskResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcAttribute(describeVpcAttributeRequest: DescribeVpcAttributeRequest?): DescribeVpcAttributeResult {
        throw Exception("unexpected call")
    }

    override fun acceptReservedInstancesExchangeQuote(acceptReservedInstancesExchangeQuoteRequest: AcceptReservedInstancesExchangeQuoteRequest?): AcceptReservedInstancesExchangeQuoteResult {
        throw Exception("unexpected call")
    }

    override fun disassociateAddress(disassociateAddressRequest: DisassociateAddressRequest?): DisassociateAddressResult {
        throw Exception("unexpected call")
    }

    override fun bundleInstance(bundleInstanceRequest: BundleInstanceRequest?): BundleInstanceResult {
        throw Exception("unexpected call")
    }

    override fun requestSpotInstances(requestSpotInstancesRequest: RequestSpotInstancesRequest?): RequestSpotInstancesResult {
        throw Exception("unexpected call")
    }

    override fun acceptVpcPeeringConnection(acceptVpcPeeringConnectionRequest: AcceptVpcPeeringConnectionRequest?): AcceptVpcPeeringConnectionResult {
        throw Exception("unexpected call")
    }

    override fun acceptVpcPeeringConnection(): AcceptVpcPeeringConnectionResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstancesListings(describeReservedInstancesListingsRequest: DescribeReservedInstancesListingsRequest?): DescribeReservedInstancesListingsResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstancesListings(): DescribeReservedInstancesListingsResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcTenancy(modifyVpcTenancyRequest: ModifyVpcTenancyRequest?): ModifyVpcTenancyResult {
        throw Exception("unexpected call")
    }

    override fun describeVolumeAttribute(describeVolumeAttributeRequest: DescribeVolumeAttributeRequest?): DescribeVolumeAttributeResult {
        throw Exception("unexpected call")
    }

    override fun detachClassicLinkVpc(detachClassicLinkVpcRequest: DetachClassicLinkVpcRequest?): DetachClassicLinkVpcResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpointServicePermissions(describeVpcEndpointServicePermissionsRequest: DescribeVpcEndpointServicePermissionsRequest?): DescribeVpcEndpointServicePermissionsResult {
        throw Exception("unexpected call")
    }

    override fun deleteEgressOnlyInternetGateway(deleteEgressOnlyInternetGatewayRequest: DeleteEgressOnlyInternetGatewayRequest?): DeleteEgressOnlyInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpointConnectionNotifications(describeVpcEndpointConnectionNotificationsRequest: DescribeVpcEndpointConnectionNotificationsRequest?): DescribeVpcEndpointConnectionNotificationsResult {
        throw Exception("unexpected call")
    }

    override fun createVpcEndpointConnectionNotification(createVpcEndpointConnectionNotificationRequest: CreateVpcEndpointConnectionNotificationRequest?): CreateVpcEndpointConnectionNotificationResult {
        throw Exception("unexpected call")
    }

    override fun deregisterImage(deregisterImageRequest: DeregisterImageRequest?): DeregisterImageResult {
        throw Exception("unexpected call")
    }

    override fun createRouteTable(createRouteTableRequest: CreateRouteTableRequest?): CreateRouteTableResult {
        throw Exception("unexpected call")
    }

    override fun setRegion(region: Region?) {
        throw Exception("unexpected call")
    }

    override fun acceptVpcEndpointConnections(acceptVpcEndpointConnectionsRequest: AcceptVpcEndpointConnectionsRequest?): AcceptVpcEndpointConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun createSubnet(createSubnetRequest: CreateSubnetRequest?): CreateSubnetResult {
        throw Exception("unexpected call")
    }

    override fun describeBundleTasks(describeBundleTasksRequest: DescribeBundleTasksRequest?): DescribeBundleTasksResult {
        throw Exception("unexpected call")
    }

    override fun describeBundleTasks(): DescribeBundleTasksResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcPeeringConnections(describeVpcPeeringConnectionsRequest: DescribeVpcPeeringConnectionsRequest?): DescribeVpcPeeringConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcPeeringConnections(): DescribeVpcPeeringConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun describeInstanceStatus(describeInstanceStatusRequest: DescribeInstanceStatusRequest?): DescribeInstanceStatusResult {
        throw Exception("unexpected call")
    }

    override fun describeInstanceStatus(): DescribeInstanceStatusResult {
        throw Exception("unexpected call")
    }

    override fun describeVpnConnections(describeVpnConnectionsRequest: DescribeVpnConnectionsRequest?): DescribeVpnConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun describeVpnConnections(): DescribeVpnConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun importKeyPair(importKeyPairRequest: ImportKeyPairRequest?): ImportKeyPairResult {
        throw Exception("unexpected call")
    }

    override fun replaceRoute(replaceRouteRequest: ReplaceRouteRequest?): ReplaceRouteResult {
        throw Exception("unexpected call")
    }

    override fun describeMovingAddresses(describeMovingAddressesRequest: DescribeMovingAddressesRequest?): DescribeMovingAddressesResult {
        throw Exception("unexpected call")
    }

    override fun describeMovingAddresses(): DescribeMovingAddressesResult {
        throw Exception("unexpected call")
    }

    override fun updateSecurityGroupRuleDescriptionsEgress(updateSecurityGroupRuleDescriptionsEgressRequest: UpdateSecurityGroupRuleDescriptionsEgressRequest?): UpdateSecurityGroupRuleDescriptionsEgressResult {
        throw Exception("unexpected call")
    }

    override fun createNetworkInterfacePermission(createNetworkInterfacePermissionRequest: CreateNetworkInterfacePermissionRequest?): CreateNetworkInterfacePermissionResult {
        throw Exception("unexpected call")
    }

    override fun attachVpnGateway(attachVpnGatewayRequest: AttachVpnGatewayRequest?): AttachVpnGatewayResult {
        throw Exception("unexpected call")
    }

    override fun createNatGateway(createNatGatewayRequest: CreateNatGatewayRequest?): CreateNatGatewayResult {
        throw Exception("unexpected call")
    }

    override fun cancelReservedInstancesListing(cancelReservedInstancesListingRequest: CancelReservedInstancesListingRequest?): CancelReservedInstancesListingResult {
        throw Exception("unexpected call")
    }

    override fun deletePlacementGroup(deletePlacementGroupRequest: DeletePlacementGroupRequest?): DeletePlacementGroupResult {
        throw Exception("unexpected call")
    }

    override fun detachVpnGateway(detachVpnGatewayRequest: DetachVpnGatewayRequest?): DetachVpnGatewayResult {
        throw Exception("unexpected call")
    }

    override fun reportInstanceStatus(reportInstanceStatusRequest: ReportInstanceStatusRequest?): ReportInstanceStatusResult {
        throw Exception("unexpected call")
    }

    override fun deleteNetworkInterfacePermission(deleteNetworkInterfacePermissionRequest: DeleteNetworkInterfacePermissionRequest?): DeleteNetworkInterfacePermissionResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcEndpoint(modifyVpcEndpointRequest: ModifyVpcEndpointRequest?): ModifyVpcEndpointResult {
        throw Exception("unexpected call")
    }

    override fun describeIdentityIdFormat(describeIdentityIdFormatRequest: DescribeIdentityIdFormatRequest?): DescribeIdentityIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstancesOfferings(describeReservedInstancesOfferingsRequest: DescribeReservedInstancesOfferingsRequest?): DescribeReservedInstancesOfferingsResult {
        throw Exception("unexpected call")
    }

    override fun describeReservedInstancesOfferings(): DescribeReservedInstancesOfferingsResult {
        throw Exception("unexpected call")
    }

    override fun runInstances(runInstancesRequest: RunInstancesRequest?): RunInstancesResult {
        throw Exception("unexpected call")
    }

    override fun authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest: AuthorizeSecurityGroupIngressRequest?): AuthorizeSecurityGroupIngressResult {
        throw Exception("unexpected call")
    }

    override fun registerImage(registerImageRequest: RegisterImageRequest?): RegisterImageResult {
        throw Exception("unexpected call")
    }

    override fun describeSecurityGroups(describeSecurityGroupsRequest: DescribeSecurityGroupsRequest?): DescribeSecurityGroupsResult {
        throw Exception("unexpected call")
    }

    override fun describeSecurityGroups(): DescribeSecurityGroupsResult {
        throw Exception("unexpected call")
    }

    override fun describeNetworkInterfaces(describeNetworkInterfacesRequest: DescribeNetworkInterfacesRequest?): DescribeNetworkInterfacesResult {
        throw Exception("unexpected call")
    }

    override fun describeNetworkInterfaces(): DescribeNetworkInterfacesResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpointConnections(describeVpcEndpointConnectionsRequest: DescribeVpcEndpointConnectionsRequest?): DescribeVpcEndpointConnectionsResult {
        throw Exception("unexpected call")
    }

    override fun describeRegions(describeRegionsRequest: DescribeRegionsRequest?): DescribeRegionsResult {
        throw Exception("unexpected call")
    }

    override fun describeRegions(): DescribeRegionsResult {
        throw Exception("unexpected call")
    }

    override fun deleteInternetGateway(deleteInternetGatewayRequest: DeleteInternetGatewayRequest?): DeleteInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun resetInstanceAttribute(resetInstanceAttributeRequest: ResetInstanceAttributeRequest?): ResetInstanceAttributeResult {
        throw Exception("unexpected call")
    }

    override fun createRoute(createRouteRequest: CreateRouteRequest?): CreateRouteResult {
        throw Exception("unexpected call")
    }

    override fun describeSubnets(describeSubnetsRequest: DescribeSubnetsRequest?): DescribeSubnetsResult {
        throw Exception("unexpected call")
    }

    override fun describeSubnets(): DescribeSubnetsResult {
        throw Exception("unexpected call")
    }

    override fun describePrefixLists(describePrefixListsRequest: DescribePrefixListsRequest?): DescribePrefixListsResult {
        throw Exception("unexpected call")
    }

    override fun describePrefixLists(): DescribePrefixListsResult {
        throw Exception("unexpected call")
    }

    override fun enableVolumeIO(enableVolumeIORequest: EnableVolumeIORequest?): EnableVolumeIOResult {
        throw Exception("unexpected call")
    }

    override fun deleteNetworkInterface(deleteNetworkInterfaceRequest: DeleteNetworkInterfaceRequest?): DeleteNetworkInterfaceResult {
        throw Exception("unexpected call")
    }

    override fun disableVpcClassicLink(disableVpcClassicLinkRequest: DisableVpcClassicLinkRequest?): DisableVpcClassicLinkResult {
        throw Exception("unexpected call")
    }

    override fun importVolume(importVolumeRequest: ImportVolumeRequest?): ImportVolumeResult {
        throw Exception("unexpected call")
    }

    override fun shutdown() {
        throw Exception("unexpected call")
    }

    override fun describeConversionTasks(describeConversionTasksRequest: DescribeConversionTasksRequest?): DescribeConversionTasksResult {
        throw Exception("unexpected call")
    }

    override fun describeConversionTasks(): DescribeConversionTasksResult {
        throw Exception("unexpected call")
    }

    override fun attachClassicLinkVpc(attachClassicLinkVpcRequest: AttachClassicLinkVpcRequest?): AttachClassicLinkVpcResult {
        throw Exception("unexpected call")
    }

    override fun deleteNetworkAcl(deleteNetworkAclRequest: DeleteNetworkAclRequest?): DeleteNetworkAclResult {
        throw Exception("unexpected call")
    }

    override fun modifyIdFormat(modifyIdFormatRequest: ModifyIdFormatRequest?): ModifyIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun cancelExportTask(cancelExportTaskRequest: CancelExportTaskRequest?): CancelExportTaskResult {
        throw Exception("unexpected call")
    }

    override fun associateAddress(associateAddressRequest: AssociateAddressRequest?): AssociateAddressResult {
        throw Exception("unexpected call")
    }

    override fun detachVolume(detachVolumeRequest: DetachVolumeRequest?): DetachVolumeResult {
        throw Exception("unexpected call")
    }

    override fun createInternetGateway(createInternetGatewayRequest: CreateInternetGatewayRequest?): CreateInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun createInternetGateway(): CreateInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun copySnapshot(copySnapshotRequest: CopySnapshotRequest?): CopySnapshotResult {
        throw Exception("unexpected call")
    }

    override fun cancelSpotFleetRequests(cancelSpotFleetRequestsRequest: CancelSpotFleetRequestsRequest?): CancelSpotFleetRequestsResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpcEndpoints(deleteVpcEndpointsRequest: DeleteVpcEndpointsRequest?): DeleteVpcEndpointsResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpnGateway(deleteVpnGatewayRequest: DeleteVpnGatewayRequest?): DeleteVpnGatewayResult {
        throw Exception("unexpected call")
    }

    override fun restoreAddressToClassic(restoreAddressToClassicRequest: RestoreAddressToClassicRequest?): RestoreAddressToClassicResult {
        throw Exception("unexpected call")
    }

    override fun createKeyPair(createKeyPairRequest: CreateKeyPairRequest?): CreateKeyPairResult {
        throw Exception("unexpected call")
    }

    override fun deleteDhcpOptions(deleteDhcpOptionsRequest: DeleteDhcpOptionsRequest?): DeleteDhcpOptionsResult {
        throw Exception("unexpected call")
    }

    override fun createVpcEndpointServiceConfiguration(createVpcEndpointServiceConfigurationRequest: CreateVpcEndpointServiceConfigurationRequest?): CreateVpcEndpointServiceConfigurationResult {
        throw Exception("unexpected call")
    }

    override fun cancelSpotInstanceRequests(cancelSpotInstanceRequestsRequest: CancelSpotInstanceRequestsRequest?): CancelSpotInstanceRequestsResult {
        throw Exception("unexpected call")
    }

    override fun detachNetworkInterface(detachNetworkInterfaceRequest: DetachNetworkInterfaceRequest?): DetachNetworkInterfaceResult {
        throw Exception("unexpected call")
    }

    override fun getConsoleOutput(getConsoleOutputRequest: GetConsoleOutputRequest?): GetConsoleOutputResult {
        throw Exception("unexpected call")
    }

    override fun modifyIdentityIdFormat(modifyIdentityIdFormatRequest: ModifyIdentityIdFormatRequest?): ModifyIdentityIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun createLaunchTemplateVersion(createLaunchTemplateVersionRequest: CreateLaunchTemplateVersionRequest?): CreateLaunchTemplateVersionResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpc(deleteVpcRequest: DeleteVpcRequest?): DeleteVpcResult {
        throw Exception("unexpected call")
    }

    override fun deleteSnapshot(deleteSnapshotRequest: DeleteSnapshotRequest?): DeleteSnapshotResult {
        throw Exception("unexpected call")
    }

    override fun createDefaultSubnet(createDefaultSubnetRequest: CreateDefaultSubnetRequest?): CreateDefaultSubnetResult {
        throw Exception("unexpected call")
    }

    override fun getHostReservationPurchasePreview(getHostReservationPurchasePreviewRequest: GetHostReservationPurchasePreviewRequest?): GetHostReservationPurchasePreviewResult {
        throw Exception("unexpected call")
    }

    override fun disableVgwRoutePropagation(disableVgwRoutePropagationRequest: DisableVgwRoutePropagationRequest?): DisableVgwRoutePropagationResult {
        throw Exception("unexpected call")
    }

    override fun associateIamInstanceProfile(associateIamInstanceProfileRequest: AssociateIamInstanceProfileRequest?): AssociateIamInstanceProfileResult {
        throw Exception("unexpected call")
    }

    override fun disassociateVpcCidrBlock(disassociateVpcCidrBlockRequest: DisassociateVpcCidrBlockRequest?): DisassociateVpcCidrBlockResult {
        throw Exception("unexpected call")
    }

    override fun createSecurityGroup(createSecurityGroupRequest: CreateSecurityGroupRequest?): CreateSecurityGroupResult {
        throw Exception("unexpected call")
    }

    override fun assignPrivateIpAddresses(assignPrivateIpAddressesRequest: AssignPrivateIpAddressesRequest?): AssignPrivateIpAddressesResult {
        throw Exception("unexpected call")
    }

    override fun createDhcpOptions(createDhcpOptionsRequest: CreateDhcpOptionsRequest?): CreateDhcpOptionsResult {
        throw Exception("unexpected call")
    }

    override fun modifyNetworkInterfaceAttribute(modifyNetworkInterfaceAttributeRequest: ModifyNetworkInterfaceAttributeRequest?): ModifyNetworkInterfaceAttributeResult {
        throw Exception("unexpected call")
    }

    override fun modifyReservedInstances(modifyReservedInstancesRequest: ModifyReservedInstancesRequest?): ModifyReservedInstancesResult {
        throw Exception("unexpected call")
    }

    override fun assignIpv6Addresses(assignIpv6AddressesRequest: AssignIpv6AddressesRequest?): AssignIpv6AddressesResult {
        throw Exception("unexpected call")
    }

    override fun createLaunchTemplate(createLaunchTemplateRequest: CreateLaunchTemplateRequest?): CreateLaunchTemplateResult {
        throw Exception("unexpected call")
    }

    override fun createEgressOnlyInternetGateway(createEgressOnlyInternetGatewayRequest: CreateEgressOnlyInternetGatewayRequest?): CreateEgressOnlyInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpnConnection(deleteVpnConnectionRequest: DeleteVpnConnectionRequest?): DeleteVpnConnectionResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotFleetRequestHistory(describeSpotFleetRequestHistoryRequest: DescribeSpotFleetRequestHistoryRequest?): DescribeSpotFleetRequestHistoryResult {
        throw Exception("unexpected call")
    }

    override fun enableVpcClassicLinkDnsSupport(enableVpcClassicLinkDnsSupportRequest: EnableVpcClassicLinkDnsSupportRequest?): EnableVpcClassicLinkDnsSupportResult {
        throw Exception("unexpected call")
    }

    override fun deleteVpcEndpointConnectionNotifications(deleteVpcEndpointConnectionNotificationsRequest: DeleteVpcEndpointConnectionNotificationsRequest?): DeleteVpcEndpointConnectionNotificationsResult {
        throw Exception("unexpected call")
    }

    override fun purchaseScheduledInstances(purchaseScheduledInstancesRequest: PurchaseScheduledInstancesRequest?): PurchaseScheduledInstancesResult {
        throw Exception("unexpected call")
    }

    override fun describeInstances(describeInstancesRequest: DescribeInstancesRequest?): DescribeInstancesResult {
        throw Exception("unexpected call")
    }

    override fun describeInstances(): DescribeInstancesResult {
        throw Exception("unexpected call")
    }

    override fun resetSnapshotAttribute(resetSnapshotAttributeRequest: ResetSnapshotAttributeRequest?): ResetSnapshotAttributeResult {
        throw Exception("unexpected call")
    }

    override fun releaseAddress(releaseAddressRequest: ReleaseAddressRequest?): ReleaseAddressResult {
        throw Exception("unexpected call")
    }

    override fun createSpotDatafeedSubscription(createSpotDatafeedSubscriptionRequest: CreateSpotDatafeedSubscriptionRequest?): CreateSpotDatafeedSubscriptionResult {
        throw Exception("unexpected call")
    }

    override fun describeExportTasks(describeExportTasksRequest: DescribeExportTasksRequest?): DescribeExportTasksResult {
        throw Exception("unexpected call")
    }

    override fun describeExportTasks(): DescribeExportTasksResult {
        throw Exception("unexpected call")
    }

    override fun allocateAddress(allocateAddressRequest: AllocateAddressRequest?): AllocateAddressResult {
        throw Exception("unexpected call")
    }

    override fun allocateAddress(): AllocateAddressResult {
        throw Exception("unexpected call")
    }

    override fun getLaunchTemplateData(getLaunchTemplateDataRequest: GetLaunchTemplateDataRequest?): GetLaunchTemplateDataResult {
        throw Exception("unexpected call")
    }

    override fun describeAccountAttributes(describeAccountAttributesRequest: DescribeAccountAttributesRequest?): DescribeAccountAttributesResult {
        throw Exception("unexpected call")
    }

    override fun describeAccountAttributes(): DescribeAccountAttributesResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpointServices(describeVpcEndpointServicesRequest: DescribeVpcEndpointServicesRequest?): DescribeVpcEndpointServicesResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpointServices(): DescribeVpcEndpointServicesResult {
        throw Exception("unexpected call")
    }

    override fun detachInternetGateway(detachInternetGatewayRequest: DetachInternetGatewayRequest?): DetachInternetGatewayResult {
        throw Exception("unexpected call")
    }

    override fun disableVpcClassicLinkDnsSupport(disableVpcClassicLinkDnsSupportRequest: DisableVpcClassicLinkDnsSupportRequest?): DisableVpcClassicLinkDnsSupportResult {
        throw Exception("unexpected call")
    }

    override fun describeFlowLogs(describeFlowLogsRequest: DescribeFlowLogsRequest?): DescribeFlowLogsResult {
        throw Exception("unexpected call")
    }

    override fun describeFlowLogs(): DescribeFlowLogsResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcEndpointServicePermissions(modifyVpcEndpointServicePermissionsRequest: ModifyVpcEndpointServicePermissionsRequest?): ModifyVpcEndpointServicePermissionsResult {
        throw Exception("unexpected call")
    }

    override fun deleteCustomerGateway(deleteCustomerGatewayRequest: DeleteCustomerGatewayRequest?): DeleteCustomerGatewayResult {
        throw Exception("unexpected call")
    }

    override fun describeScheduledInstanceAvailability(describeScheduledInstanceAvailabilityRequest: DescribeScheduledInstanceAvailabilityRequest?): DescribeScheduledInstanceAvailabilityResult {
        throw Exception("unexpected call")
    }

    override fun modifyVpcEndpointServiceConfiguration(modifyVpcEndpointServiceConfigurationRequest: ModifyVpcEndpointServiceConfigurationRequest?): ModifyVpcEndpointServiceConfigurationResult {
        throw Exception("unexpected call")
    }

    override fun disassociateIamInstanceProfile(disassociateIamInstanceProfileRequest: DisassociateIamInstanceProfileRequest?): DisassociateIamInstanceProfileResult {
        throw Exception("unexpected call")
    }

    override fun rejectVpcPeeringConnection(rejectVpcPeeringConnectionRequest: RejectVpcPeeringConnectionRequest?): RejectVpcPeeringConnectionResult {
        throw Exception("unexpected call")
    }

    override fun describeImportImageTasks(describeImportImageTasksRequest: DescribeImportImageTasksRequest?): DescribeImportImageTasksResult {
        throw Exception("unexpected call")
    }

    override fun describeImportImageTasks(): DescribeImportImageTasksResult {
        throw Exception("unexpected call")
    }

    override fun importInstance(importInstanceRequest: ImportInstanceRequest?): ImportInstanceResult {
        throw Exception("unexpected call")
    }

    override fun describeEgressOnlyInternetGateways(describeEgressOnlyInternetGatewaysRequest: DescribeEgressOnlyInternetGatewaysRequest?): DescribeEgressOnlyInternetGatewaysResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotPriceHistory(describeSpotPriceHistoryRequest: DescribeSpotPriceHistoryRequest?): DescribeSpotPriceHistoryResult {
        throw Exception("unexpected call")
    }

    override fun describeSpotPriceHistory(): DescribeSpotPriceHistoryResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpoints(describeVpcEndpointsRequest: DescribeVpcEndpointsRequest?): DescribeVpcEndpointsResult {
        throw Exception("unexpected call")
    }

    override fun describeVpcEndpoints(): DescribeVpcEndpointsResult {
        throw Exception("unexpected call")
    }

    override fun describeAggregateIdFormat(describeAggregateIdFormatRequest: DescribeAggregateIdFormatRequest?): DescribeAggregateIdFormatResult {
        throw Exception("unexpected call")
    }

    override fun copyImage(copyImageRequest: CopyImageRequest?): CopyImageResult {
        throw Exception("unexpected call")
    }

    override fun createFpgaImage(createFpgaImageRequest: CreateFpgaImageRequest?): CreateFpgaImageResult {
        throw Exception("unexpected call")
    }

    override fun describeInstanceCreditSpecifications(describeInstanceCreditSpecificationsRequest: DescribeInstanceCreditSpecificationsRequest?): DescribeInstanceCreditSpecificationsResult {
        throw Exception("unexpected call")
    }

    override fun allocateHosts(allocateHostsRequest: AllocateHostsRequest?): AllocateHostsResult {
        throw Exception("unexpected call")
    }
}