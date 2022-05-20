package com.atlassian.performance.tools.aws.api

import com.amazonaws.services.cloudformation.model.*
import com.amazonaws.services.cloudformation.model.StackStatus.*
import com.atlassian.performance.tools.aws.PageScrollingStackEventsQuerier
import org.apache.commons.codec.binary.Hex
import org.apache.logging.log4j.LogManager
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant.now
import java.util.concurrent.TimeUnit

/**
 * @property [aws] Serves the stack.
 * @property [cloudformationTemplate] Defines the resources to provision in a YAML format.
 * @property [parameters] Parametrize the [cloudformationTemplate].
 * @property [detectionTimeout] Gives time to detect an existing matching stack for reuse.
 * @property [pollingTimeout] Gives time for the stack to transition to a successful state.
 */
data class StackFormula @JvmOverloads constructor(
    private val investment: Investment,
    private val aws: Aws,
    private val cloudformationTemplate: String,
    private val parameters: List<Parameter> = listOf(),
    private val detectionTimeout: Duration = Duration.ofMinutes(3),
    private val pollingTimeout: Duration = Duration.ofMinutes(30)
) {

    private val logger = LogManager.getLogger(this::class.java)

    private val hash by lazy {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(cloudformationTemplate.toByteArray())
        val templateHash = Hex.encodeHexString(digest.digest())
        return@lazy StackHash(templateHash, parameters)
    }

    private val stackName = "${investment.reuseKey()}-${Integer.toHexString(hash.hashCode())}"

    private data class StackHash(
        val templateHash: String,
        val parameters: List<Parameter>
    )

    /**
     * Provisions a CloudFormation stack using [aws] and tagging it with [investment].
     * The stack resources are defined by [cloudformationTemplate] parametrized by [parameters].
     * Tries to detect an existing matching stack to reuse, unless [detectionTimeout] runs out.
     * Polls the stack status to reach a usable state for up to [pollingTimeout].
     */
    fun provision(): ProvisionedStack {
        ensureExistence()
        return waitUntilOperational()
    }

    private fun ensureExistence() {
        try {
            create()
        } catch (e: Exception) {
            if (exists()) {
                logger.debug("Stack already exists $stackName")
            } else {
                throw Exception("Provisioning $stackName failed", e)
            }
        }
    }

    private fun exists(): Boolean = find() != null

    private fun find(): Stack? {
        return try {
            aws
                .batchingCfn
                .findStack(stackName)
                .get(detectionTimeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            logger.debug("Failed to find stack: ${e.message}")
            null
        }
    }

    private fun create() {
        aws.cloudformation.createStack(
            CreateStackRequest()
                .withTags(investment.tag().map { it.toCloudformation() })
                .withStackName(stackName)
                .withTemplateBody(cloudformationTemplate)
                .withParameters(parameters)
                .withCapabilities(Capability.CAPABILITY_IAM)
        )
    }

    private fun waitUntilOperational(): ProvisionedStack {
        val deadline = now() + pollingTimeout
        while (now() < deadline) {
            val stack = aws.batchingCfn.findStack(stackName).get()
            if (stack == null) {
                logger.debug("Stack $stackName is not visible yet")
                continue
            }
            val status = fromValue(stack.stackStatus)
            when (status) {
                CREATE_IN_PROGRESS, UPDATE_IN_PROGRESS -> {
                    logger.debug("Stack $stackName is still starting up: $status")
                }
                CREATE_COMPLETE, UPDATE_COMPLETE -> {
                    logger.debug("Stack $stackName is operational: $status")
                    return ProvisionedStack(stack, aws)
                }
                else -> {
                    logger.error("Stack $stack failed: $status")
                    aws.stackNanny.takeCare(stackName)
                    try {
                        PageScrollingStackEventsQuerier.Builder(aws.cloudformation).build()
                            .getEvents(stackId = stack.stackId).let { stackEvents ->
                                throw Exception("Stack $stackName creation failed: $stack; Stack creation events: $stackEvents")
                            }
                    } catch (e: Exception) {
                        throw Exception("Stack $stackName creation failed, but we failed to capture failure reason: $stack", e)
                    }
                }
            }
        }
        throw Exception("Stack $stackName provisioning timed out: $pollingTimeout")
    }
}