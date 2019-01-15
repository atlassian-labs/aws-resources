package com.atlassian.performance.tools.aws.api

import com.atlassian.performance.tools.aws.IntegrationTestRuntime
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.*

class StorageIT {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    @JvmField
    @Rule
    val folder = TemporaryFolder()

    @Test
    fun shouldUploadAndDownload() {
        val expectedFileName = "test-artifact.txt"
        val expectedFile = File(javaClass.getResource(expectedFileName).toURI())
        val expectedFileContent = expectedFile.readText()
        val nonce = "StorageTest-${UUID.randomUUID()}"
        logger.info("Generated nonce for the test: $nonce.")
        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)

        storage.upload(expectedFile)
        val actualFile = storage.download(folder.newFolder().toPath()).resolve(expectedFileName).toFile()
        val actualFileContent = actualFile.readText()

        assertThat(actualFileContent).isEqualTo(expectedFileContent)
    }

    @Test
    fun shouldUploadAndDownloadFileWithSpecialCharacters() {
        val expectedFileName = "test-artifact ::.txt"
        val expectedFile = File(javaClass.getResource(expectedFileName).toURI())
        val expectedFileContent = expectedFile.readText()
        val nonce = "StorageTest%% : : -${UUID.randomUUID()}"
        logger.info("Generated nonce for the test: $nonce.")
        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)

        storage.upload(expectedFile)
        val actualFile = storage.download(folder.newFolder().toPath()).resolve(expectedFileName).toFile()
        val actualFileContent = actualFile.readText()

        assertThat(actualFileContent).isEqualTo(expectedFileContent)
    }
}