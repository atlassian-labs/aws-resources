import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val kotlinVersion = "1.2.30"

plugins {
    kotlin("jvm").version("1.2.30")
    `java-library`
    id("com.atlassian.performance.tools.gradle-release").version("0.4.1")
}

tasks.withType<Test>{
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = TestExceptionFormat.FULL
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
        eachDependency {
            when (requested.module.toString()) {
                "commons-logging:commons-logging" -> useVersion("1.2")
                "org.slf4j:slf4j-api" -> useVersion("1.8.0-alpha2")
            }
        }
    }
}

dependencies {
    api("com.atlassian.performance.tools:ssh:[1.0.0,2.0.0)")
    api("com.github.stephenc.jcip:jcip-annotations:1.0-1")
    implementation("com.atlassian.performance.tools:concurrency:[1.0.0,2.0.0)")
    implementation("com.atlassian.performance.tools:io:[1.0.0,2.0.0)")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
    implementation("commons-codec:commons-codec:1.10")
    aws(
        "ec2",
        "s3",
        "iam",
        "sts",
        "cloudformation",
        "elasticloadbalancing",
        "support"
    ).forEach { api(it) }

    log4jCore().forEach { implementation(it) }

    testCompile("junit:junit:4.12")
    testCompile("org.hamcrest:hamcrest-library:1.3")
}

fun aws(
    vararg modules: String
): List<String> = modules
    .map { module ->
        "com.amazonaws:aws-java-sdk-$module:1.11.298"
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
    "org.apache.logging.log4j:log4j-$module:2.10.0"
}

fun jaxb(): List<String> = listOf(
    "org.glassfish.jaxb:jaxb-runtime:2.3.0",
    "javax.activation:activation:1.1.1"
)

val wrapper = tasks["wrapper"] as Wrapper
wrapper.gradleVersion = "4.9"
wrapper.distributionType = Wrapper.DistributionType.ALL