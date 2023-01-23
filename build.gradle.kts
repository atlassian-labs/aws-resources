import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val kotlinVersion = "1.2.70"

plugins {
    kotlin("jvm").version("1.7.10")
    `java-library`
    id("com.atlassian.performance.tools.gradle-release").version("0.7.1")
}

tasks.withType<Test>{
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = TestExceptionFormat.FULL
}

configurations.all {
    // Workaround harvested from https://github.com/atlassian/virtual-users/pull/50/commits/24cdab615dd4c9d7a58cfeb38c1df3585324de6d
    if (name.startsWith("kotlinCompiler")) {
        return@all
    }
    resolutionStrategy {
        activateDependencyLocking()
        failOnVersionConflict()
        eachDependency {
            when (requested.module.toString()) {
                "commons-logging:commons-logging" -> useVersion("1.2")
                "org.slf4j:slf4j-api" -> useVersion("1.8.0-alpha2")
            }
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
            }
        }
    }
}

dependencies {
    api("com.atlassian.performance.tools:ssh:[1.0.0,3.0.0)")
    api("com.github.stephenc.jcip:jcip-annotations:1.0-1")
    implementation("com.atlassian.performance.tools:concurrency:[1.0.0,2.0.0)")
    implementation("com.atlassian.performance.tools:io:[1.2.0,2.0.0)")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
    implementation("commons-codec:commons-codec:1.11")
    aws(
        "ec2",
        "s3",
        "rds",
        "iam",
        "sts",
        "cloudformation",
        "elasticloadbalancing",
        "support"
    ).forEach { api(it) }

    log4jCore().forEach { implementation(it) }

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

fun aws(
    vararg modules: String
): List<String> = modules
    .map { module ->
        "com.amazonaws:aws-java-sdk-$module:1.11.817"
    }
    .plus(log4j("jcl"))
    .plus(jaxb())

fun log4jCore(): List<String> = log4j(
    "api",
    "core",
    "slf4j-impl"
)

fun log4j(
    vararg modules: String
): List<String> = modules.map { module ->
    "org.apache.logging.log4j:log4j-$module:2.17.1"
}

fun jaxb(): List<String> = listOf(
    "org.glassfish.jaxb:jaxb-runtime:2.3.0",
    "javax.activation:activation:1.1.1"
)

val cleanLeftovers = task<Test>("awsCleanLeftovers")
cleanLeftovers.description = "Deletes every stack that exceeded its lifespan."
cleanLeftovers.useJUnitPlatform {
    includeTags("clean-leftovers")
}


tasks.getByName("test", Test::class).apply {
    useJUnitPlatform {
        exclude("**/*IT.class")
        excludeTags("clean-leftovers")
    }
}

val testIntegration = task<Test>("testIntegration") {
    useJUnitPlatform {
        include("**/*IT.class")
        excludeTags("clean-leftovers")
    }
    // https://junit.org/junit5/docs/snapshot/user-guide/#writing-tests-parallel-execution
    systemProperty("junit.jupiter.execution.parallel.enabled", true)
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
}

tasks["check"].dependsOn(testIntegration)

val wrapper = tasks["wrapper"] as Wrapper
wrapper.gradleVersion = "7.6"
wrapper.distributionType = Wrapper.DistributionType.ALL
