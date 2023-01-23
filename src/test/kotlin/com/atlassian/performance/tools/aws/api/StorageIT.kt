package com.atlassian.performance.tools.aws.api

import com.atlassian.performance.tools.aws.IntegrationTestRuntime
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.util.*

class StorageIT {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    @Test
    fun shouldUploadAndDownload(@TempDir folder: Path) {
        val expectedFileName = "test-artifact.txt"
        val expectedFile = File(javaClass.getResource(expectedFileName).toURI())
        val expectedFileContent = expectedFile.readText()
        val nonce = "StorageTest-${UUID.randomUUID()}"
        logger.info("Generated nonce for the test: $nonce.")
        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)

        storage.upload(expectedFile)
        val actualFile = storage.download(folder).resolve(expectedFileName).toFile()
        val actualFileContent = actualFile.readText()

        assertThat(actualFileContent).isEqualTo(expectedFileContent)
    }

    @Test
    fun shouldUploadAndDownloadFileWithSpecialCharacters(@TempDir folder: Path) {
        val expectedFileName = "test-artifact ::.txt"
        val expectedFile = File(javaClass.getResource(expectedFileName).toURI())
        val expectedFileContent = expectedFile.readText()
        val nonce = "StorageTest%% : : -${UUID.randomUUID()}"
        logger.info("Generated nonce for the test: $nonce.")
        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)

        storage.upload(expectedFile)
        val actualFile = storage.download(folder).resolve(expectedFileName).toFile()
        val actualFileContent = actualFile.readText()

        assertThat(actualFileContent).isEqualTo(expectedFileContent)
    }

    @Test
    fun shouldUploadAndDownloadSymmetrically(@TempDir upload: Path, @TempDir download: Path) {
        val subfolder = upload.resolve("subfolder")
        subfolder.toFile().mkdir()
        val file = subfolder.resolve("testfile.txt")
        file.toFile().createNewFile()
        val nonce = "StorageTest.shouldUploadAndDownloadSymmetrically.${UUID.randomUUID()}"

        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)
        storage.upload(upload.toFile())
        storage.download(download).toFile()

        assertThat(download).exists()
        assertThat(download.toFile().list()).isEqualTo(upload.toFile().list())
    }

    @Test
    fun shouldBeAbleToCacheResults(@TempDir upload: Path, @TempDir download: Path, @TempDir cache: Path) {
        val file = upload.resolve("testfile.txt").toFile()
        file.createNewFile()
        val nonce = "StorageTest.shouldBeAbleToCacheResults.${UUID.randomUUID()}"
        val storage = IntegrationTestRuntime.aws.resultsStorage(nonce)
        storage.upload(upload.toFile())
        storage.download(download).toFile()

        val cacheNonce = "StorageTest.shouldBeAbleToCacheResults.${UUID.randomUUID()}"
        val cacheStorage = IntegrationTestRuntime.aws.resultsStorage(cacheNonce)
        cacheStorage.upload(download.toFile())
        val cachedResults = cacheStorage.download(cache).toFile()

        assertThat(cachedResults).exists()
        assertThat(cachedResults.list()).isEqualTo(upload.toFile().list())
        assertThat(cachedResults.list()).isEqualTo(download.toFile().list())
    }
}