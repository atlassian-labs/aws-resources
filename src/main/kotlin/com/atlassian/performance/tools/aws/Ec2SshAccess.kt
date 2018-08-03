package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.*

internal class Ec2SshAccess(
    private val ec2: AmazonEC2,
    private val awaitingEc2: AwaitingEc2
) {

    /**
     * @param vpcId if null, the default VPC will be used
     */
    fun getSecurityGroup(
        investment: Investment,
        vpcId: String? = null
    ): SecurityGroup {
        val securityGroup = awaitingEc2.allocateSecurityGroup(
            investment,
            CreateSecurityGroupRequest()
                .withGroupName("${investment.reuseKey()}-Ssh")
                .withDescription("Enables SSH access")
                .withVpcId(vpcId)
        )
        ec2.authorizeSecurityGroupIngress(
            AuthorizeSecurityGroupIngressRequest()
                .withGroupId(securityGroup.groupId)
                .withIpPermissions(
                    IpPermission()
                        .withIpProtocol("tcp")
                        .withFromPort(22)
                        .withToPort(22)
                        .withIpv4Ranges(
                            IpRange().withCidrIp("0.0.0.0/0")
                        )
                )
        )
        return securityGroup
    }
}