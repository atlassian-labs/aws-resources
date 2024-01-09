package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.*
import com.amazonaws.waiters.WaiterParameters
import com.atlassian.performance.tools.aws.Ec2Instance
import com.atlassian.performance.tools.aws.Ec2SshAccess
import com.atlassian.performance.tools.ssh.api.Ssh
import com.atlassian.performance.tools.ssh.api.SshHost
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.time.Instant.now

internal typealias InstanceLaunchMod = (RunInstancesRequest) -> RunInstancesRequest

/**
 * Instances started with this class will by default terminate after an instance initiated shutdown.
 */
class AwaitingEc2(
    private val ec2: AmazonEC2,
    private val terminationBatchingEc2: TerminationBatchingEc2,
    private val instanceNanny: InstanceNanny,
    private val defaultAmi: String
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * Logs SSH command to connect to the instance.
     * @param vpcId if null, the default VPC will be used
     */
    fun allocateInstance(
        investment: Investment,
        key: SshKey,
        vpcId: String?,
        customizeLaunch: InstanceLaunchMod
    ): SshInstance {
        val sshAccess = Ec2SshAccess(ec2, this).getSecurityGroup(investment, vpcId)
        val launchRequest = customizeLaunch(launchDefaults(key, investment, sshAccess))
        val startingInstance = startInstance(launchRequest)
        ec2.waiters().instanceRunning().run(WaiterParameters(startingInstance))
        val startedInstance = ec2
            .describeInstances(startingInstance)
            .reservations
            .flatMap { it.instances }
            .single()
        key.file.facilitateSsh(startedInstance.publicIpAddress)
        return SshInstance(
            ssh = Ssh(
                host = SshHost(startedInstance.publicIpAddress, launchRequest.additionalInfo, key.file.path),
                connectivityPatience = 4
            ),
            resource = DependentResources(
                user = Ec2Instance(startedInstance, terminationBatchingEc2),
                dependency = Ec2SecurityGroup(sshAccess, ec2)
            ),
            instance = startedInstance
        )
    }

    private fun launchDefaults(
        key: SshKey,
        investment: Investment,
        sshAccess: SecurityGroup
    ): RunInstancesRequest {
        return RunInstancesRequest()
            .withMinCount(1)
            .withMaxCount(1)
            .withInstanceType(InstanceType.T3Nano)
            .withImageId(defaultAmi)
            .withAdditionalInfo("ubuntu")
            .withInstanceInitiatedShutdownBehavior(ShutdownBehavior.Terminate)
            .withKeyName(key.remote.name)
            .withSecurityGroupIds(sshAccess.groupId)
            .withTagSpecifications(
                TagSpecification()
                    .withResourceType(ResourceType.Instance)
                    .withTags(investment.tag().map { it.toEc2() })
            )
    }

    private fun startInstance(
        launch: RunInstancesRequest
    ): DescribeInstancesRequest {
        val response = try {
            ec2.runInstances(launch)
        } catch (e: Exception) {
            instanceNanny.takeCare(e, launch)
        }
        val instance = response
            .reservation
            .instances
            .single()
        return DescribeInstancesRequest().withInstanceIds(instance.instanceId)
    }

    fun allocateSecurityGroup(
        investment: Investment,
        request: CreateSecurityGroupRequest
    ): SecurityGroup {
        val tagSpec = TagSpecification()
            .withTags(investment.tag().map { it.toEc2() })
            .withResourceType(ResourceType.SecurityGroup)
        return allocateSecurityGroup(request.withTagSpecifications(tagSpec))
    }

    private fun allocateSecurityGroup(
        request: CreateSecurityGroupRequest
    ): SecurityGroup {
        val securityGroup = ec2.createSecurityGroup(request)
        val refresh = DescribeSecurityGroupsRequest().withGroupIds(securityGroup.groupId)
        val timeout = Duration.ofSeconds(30)
        val deadline = now() + timeout
        do {
            try {
                val results = ec2
                    .describeSecurityGroups(refresh)
                    .securityGroups
                if (results.size == 1) {
                    return results.single()
                }
            } catch (e: Exception) {
                logger.debug("Failed to find security group $refresh", e)
            }
            Thread.sleep(Duration.ofSeconds(5).toMillis())
        } while (now().isBefore(deadline))
        throw Exception("Failed to find with $refresh within $timeout")
    }
}
