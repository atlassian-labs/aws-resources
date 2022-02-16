package com.atlassian.performance.tools.aws.api

import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import java.util.*
import com.amazonaws.services.cloudformation.model.Tag as CfnTag
import com.amazonaws.services.ec2.model.Tag as Ec2Tag

/**
 * Describes an investment:
 * - value proposition
 * - cost factors
 * - accountability tracking
 *
 * @param useCase the value proposition for the investment
 * @param lifespan pessimistic estimate of the duration of the investment, higher values mean higher cost
 * @param disposable if true, then the investment can be cancelled even before [lifespan] and get its cost reduced
 * @param reuseKey if a new investment would have the same reuse key as an existing investment, the old one can be
 *                 reused, reducing the cost
 */
data class Investment(
    private val useCase: String,
    val lifespan: Duration,
    private val disposable: Boolean = true,
    val reuseKey: () -> String = { "jpt-${UUID.randomUUID()}" }
) {
    /**
     * @return tags useful for tracking accountability of the investment
     */
    fun tag(): List<Tag> = tagAtlassianAwsAccountability() + tagLifecycle() + tagInitiator()

    /**
     * @return tags required by all Atlassian AWS accounts
     */
    private fun tagAtlassianAwsAccountability(): List<Tag> = listOf(
        Tag("Name", "Jira Performance Tests"),
        Tag("service_name", useCase),
        Tag("business_unit", "Engineering-Server"),
        Tag("resource_owner", "mgrzaslewicz")
    )

    /**
     * @return tags for tracking lifecycle of the investment
     */
    private fun tagLifecycle(): List<Tag> = listOfNotNull(
        if (disposable) Tag(disposableKey, "true") else null,
        Tag(lifespanKey, lifespan.toString()),
        Tag(expiryKey, (now() + lifespan).toString())
    )

    /**
     * @return tags tracking the source of investment
     */
    private fun tagInitiator(): List<Tag> {
        val bambooResultKey = System.getenv("bamboo_buildResultKey")
        return if (bambooResultKey.isNullOrBlank()) {
            listOf(
                Tag(userKey, currentUser())
            )
        } else {
            listOf(
                Tag(userKey, "bamboo-agent"),
                Tag(bambooBuildKey, bambooResultKey)
            )
        }
    }

    companion object TagKeys {
        const val lifespanKey = "lifespan"
        private const val expiryKey = "expiry"
        const val userKey = "os_user_name"
        const val bambooBuildKey = "bamboo_result_key"
        const val disposableKey = "disposable"

        fun parseLifespan(
            tags: List<Tag>
        ): Duration? = tags
            .filter { it.key == lifespanKey }
            .mapNotNull { parseDurationOrNull(it.value) }
            .firstOrNull()

        fun parseExpiry(
            tags: List<Tag>
        ): Instant? = tags
            .filter { it.key == expiryKey }
            .mapNotNull { parseInstantOrNull(it.value) }
            .firstOrNull()

        private fun parseDurationOrNull(
            duration: String
        ): Duration? = try {
            Duration.parse(duration)
        } catch (e: Exception) {
            null
        }

        private fun parseInstantOrNull(
            instant: String
        ): Instant? = try {
            Instant.parse(instant)
        } catch (e: Exception) {
            null
        }
    }
}

data class Tag(
    val key: String,
    val value: String
) {
    constructor(tag: CfnTag) : this(tag.key, tag.value)
    constructor(tag: Ec2Tag) : this(tag.key, tag.value)

    fun toCloudformation(): CfnTag = CfnTag().withKey(key).withValue(value)
    fun toEc2(): Ec2Tag = Ec2Tag(key, value)
}

fun currentUser(): String = System.getenv("USER") ?: System.getenv("USERNAME") ?: "UNKNOWN"