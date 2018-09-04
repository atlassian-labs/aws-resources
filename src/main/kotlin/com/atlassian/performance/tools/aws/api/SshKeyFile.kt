package com.atlassian.performance.tools.aws.api

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission

data class SshKeyFile(
    val path: Path
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    fun facilitateSsh(
        hostIp: String
    ) {
        logger.debug("Remote access command:\n\tssh -i '$path' ubuntu@$hostIp")
    }

    fun fixPermissions() {
        Files.getFileAttributeView(path, PosixFileAttributeView::class.java)?.setPermissions(
            setOf(PosixFilePermission.OWNER_READ)
        )
    }
}