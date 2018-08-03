package com.atlassian.performance.tools.aws

import com.amazonaws.services.cloudformation.model.Capability
import com.amazonaws.services.cloudformation.model.CreateStackRequest
import com.amazonaws.services.cloudformation.model.Parameter
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.StackStatus.*
import org.apache.commons.codec.binary.Hex
import org.apache.logging.log4j.LogManager
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant.now

data class StackFormula(
    private val investment: Investment,
    private val aws: Aws,
    private val cloudformationTemplate: String,
    private val parameters: List<Parameter> = listOf()
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
                .batchingCloudformation
                .findStack(stackName)
                .get()
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
                .withDisableRollback(true)
        )
    }

    private fun waitUntilOperational(): ProvisionedStack {
        val timeout = Duration.ofMinutes(30)
        val deadline = now() + timeout
        while (now() < deadline) {
            val stack = aws.batchingCloudformation.findStack(stackName).get()
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
                    throw Exception("Stack $stackName creation failed: $stack")
                }
            }
        }
        throw Exception("Stack $stackName provisioning timed out: $timeout")
    }
}