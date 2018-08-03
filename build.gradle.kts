plugins {
    kotlin("jvm").version(Versions.kotlin)
    maven
}

maven{
    group = "com.atlassian.test.performance"
    version = "0.0.1-SNAPSHOT"
}

dependencies {
    compile(Libs.concurrency)
    compile(Libs.io)
    compile(Libs.ssh)

    compile(Libs.junit)
    compile(Libs.hamcrest)
    compile(Libs.kotlinStandard)
    compile(Libs.jcip)
    compile(Libs.commonsCodec)
    Libs.aws(
        "ec2",
        "s3",
        "iam",
        "sts",
        "cloudformation",
        "elasticloadbalancing",
        "support"
    ).forEach { compile(it) }

    Libs.log4jCore().forEach { compile(it) }
}