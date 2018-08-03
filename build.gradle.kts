val kotlinVersion = "1.2.30"

plugins {
    kotlin("jvm").version("1.2.30")
    id("com.atlassian.performance.tools.gradle-release").version("0.0.2")
}

dependencies {
    compile("com.atlassian.performance.tools:concurrency:0.0.1")
    compile("com.atlassian.performance.tools:io:0.0.1")
    compile("com.atlassian.performance.tools:ssh:0.1.0")

    compile("junit:junit:4.12")
    compile("org.hamcrest:hamcrest-library:1.3")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
    compile("net.jcip:jcip-annotations:1.0")
    compile("commons-codec:commons-codec:1.10")
    aws(
        "ec2",
        "s3",
        "iam",
        "sts",
        "cloudformation",
        "elasticloadbalancing",
        "support"
    ).forEach { compile(it) }

    log4jCore().forEach { compile(it) }
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