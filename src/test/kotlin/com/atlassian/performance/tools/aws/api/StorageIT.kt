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

    @Test
    fun shouldUploadAndDownloadSymmetrically() {
        val upload = folder.newFolder("actualFolder")
        val subfolder = upload.resolve("subfolder")
        subfolder.mkdir()
        val file = subfolder.resolve("testfile.txt")
        file.createNewFile()
        val nonce = "StorageTest.shouldUploadAndDownloadSymmetrically.${UUID.randomUUID()}"

        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)
        storage.upload(upload)
        val download = storage.download(folder.newFolder("expectedFolder").toPath()).toFile()

        assertThat(download).exists()
        assertThat(download.list()).isEqualTo(upload.list())
    }

    @Test
    fun shouldBeAbleToCacheResults() {
        val upload = folder.newFolder("actualFolder")
        val subfolder = upload.resolve("subfolder")
        subfolder.mkdir()
        val file = subfolder.resolve("testfile.txt")
        file.createNewFile()
        val nonce = "StorageTest.shouldBeAbleToCacheResults.${UUID.randomUUID()}"
        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)
        storage.upload(upload)
        val download = storage.download(folder.newFolder("download").toPath()).toFile()

        val cacheNonce = "StorageTest.shouldBeAbleToCacheResults.${UUID.randomUUID()}"
        val cache = IntegrationTestRuntime.aws.resultsStorage(cacheNonce)
        cache.upload(download)
        val cachedResults = cache.download(folder.newFolder("cache").toPath()).toFile()

        assertThat(cachedResults).exists()
        assertThat(cachedResults.list()).isEqualTo(upload.list())
        assertThat(cachedResults.list()).isEqualTo(download.list())
    }
}