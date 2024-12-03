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
 */
data class Investment
private constructor(
    private val useCase: String,
    val lifespan: Duration,
    private val disposable: Boolean = true,
    val reuseKey: () -> String = { "jpt-${UUID.randomUUID()}" },
    private val resourceOwner: String = defaultResourceOwner
) {
    /**
     *
     * @param useCase the value proposition for the investment
     * @param lifespan pessimistic estimate of the duration of the investment, higher values mean higher cost
     * @param disposable if true, then the investment can be cancelled even before [lifespan] and get its cost reduced
     * @param reuseKey if a new investment would have the same reuse key as an existing investment, the old one can be
     *                 reused, reducing the cost
     */
    @Deprecated(
        message = "Use Builder instead. Public constructor will be be removed in the next major version.",
        replaceWith = ReplaceWith(
            expression = "Investment.Builder(useCase = useCase, lifespan = lifespan)" +
                ".disposable(disposable)" +
                ".reuseKey(reuseKey)" +
                ".build()"
        )
    )
    constructor(
        useCase: String,
        lifespan: Duration,
        disposable: Boolean = true,
        reuseKey: () -> String = { "jpt-${UUID.randomUUID()}" }
    ) : this(useCase, lifespan, disposable, reuseKey, defaultResourceOwner)

    /**
     * @return tags useful for tracking accountability of the investment
     */
    fun tag(): List<Tag> = tagAtlassianAwsAccountability() + tagLifecycle() + tagInitiator()

    /**
     * Added for backwards compatibility.
     * TODO: To be removed in the next major version.
     */
    fun copy(
        useCase: String = this.useCase,
        lifespan: Duration = this.lifespan,
        disposable: Boolean = this.disposable,
        reuseKey: () -> String = this.reuseKey
    ): Investment {
        return Investment(
            useCase = useCase,
            lifespan = lifespan,
            disposable = disposable,
            reuseKey = reuseKey
        )
    }

    /**
     * @return tags required by all Atlassian AWS accounts
     */
    private fun tagAtlassianAwsAccountability(): List<Tag> = listOf(
        Tag("Name", "Jira Performance Tests"),
        Tag("service_name", useCase),
        Tag("business_unit", "Engineering-Server"),
        Tag("resource_owner", resourceOwner)
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

    /**
     * Describes an investment:
     * - value proposition
     * - cost factors
     * - accountability tracking
     *
     * @param useCase the value proposition for the investment
     * @param lifespan pessimistic estimate of the duration of the investment, higher values mean higher cost
     */
    class Builder(
        private val useCase: String,
        private val lifespan: Duration
    ) {
        private var disposable: Boolean = true
        private var reuseKey: () -> String = { "jpt-${UUID.randomUUID()}" }
        private var resourceOwner: String = defaultResourceOwner

        /**
         * @param disposable if true, then the investment can be cancelled even before [lifespan] and get its cost reduced
         */
        fun disposable(disposable: Boolean) = apply { this.disposable = disposable }
        /**
         * @param reuseKey if a new investment would have the same reuse key as an existing investment, the old one can be
         *                 reused, reducing the cost
         */
        fun reuseKey(reuseKey: () -> String) = apply { this.reuseKey = reuseKey }
        /**
         * @param resourceOwner AWS resource owner, defaults to current Atlassian JPT maintainer
         */
        fun resourceOwner(resourceOwner: String) = apply { this.resourceOwner = resourceOwner }

        fun build(): Investment {
            return Investment(
                useCase = useCase,
                lifespan = lifespan,
                disposable = disposable,
                reuseKey = reuseKey,
                resourceOwner = resourceOwner
            )
        }
    }

    companion object TagKeys {
        const val lifespanKey = "lifespan"
        private const val expiryKey = "expiry"
        const val userKey = "os_user_name"
        const val bambooBuildKey = "bamboo_result_key"
        const val disposableKey = "disposable"
        private const val defaultResourceOwner = "jforemski"

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