package com.atlassian.performance.tools.aws.api

import com.atlassian.performance.tools.aws.IntegrationTestRuntime.aws
import com.atlassian.performance.tools.io.api.ensureDirectory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.util.UUID.randomUUID

class StorageIT {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    @Test
    fun shouldUploadAndDownload(@TempDir folder: Path) {
        // given
        val expectedFileName = "test-artifact.txt"
        val expectedFile = File(javaClass.getResource(expectedFileName)!!.toURI())
        val expectedFileContent = expectedFile.readText()
        val nonce = "StorageTest-${randomUUID()}"
        logger.info("Generated nonce for the test: $nonce.")
        val storage = aws.resultsStorage(nonce)

        // when
        storage.upload(expectedFile)
        val actualFile = storage.download(folder).resolve(expectedFileName).toFile()
        val actualFileContent = actualFile.readText()

        // then
        assertThat(actualFileContent).isEqualTo(expectedFileContent)
    }

    @Test
    fun shouldUploadAndDownloadFileWithSpecialCharacters(@TempDir folder: Path) {
        // given
        val expectedFileName = "test-artifact ::.txt"
        val expectedFile = File(javaClass.getResource(expectedFileName)!!.toURI())
        val expectedFileContent = expectedFile.readText()
        val nonce = "StorageTest%% : : -${randomUUID()}"
        logger.info("Generated nonce for the test: $nonce.")
        val storage = aws.resultsStorage(nonce)

        // when
        storage.upload(expectedFile)
        val actualFile = storage.download(folder).resolve(expectedFileName).toFile()
        val actualFileContent = actualFile.readText()

        // then
        assertThat(actualFileContent).isEqualTo(expectedFileContent)
    }

    @Test
    fun shouldUploadAndDownloadSymmetrically(@TempDir folder: Path) {
        // given
        val upload = folder.resolve("upload").ensureDirectory()
        val download = folder.resolve("download").ensureDirectory()
        val subfolder = upload.resolve("subfolder").ensureDirectory()
        val file = subfolder.resolve("testfile.txt")
        file.toFile().createNewFile()
        val nonce = "StorageTest.shouldUploadAndDownloadSymmetrically.${randomUUID()}"

        // when
        val storage = aws.resultsStorage(nonce)
        storage.upload(upload.toFile())
        storage.download(download).toFile()

        // then
        assertThat(download).exists()
        assertThat(download.toFile().list()).isEqualTo(upload.toFile().list())
    }

    @Test
    fun shouldBeAbleToCacheResults(@TempDir folder: Path) {
        // given
        val upload = folder.resolve("upload").ensureDirectory()
        val download = folder.resolve("download").ensureDirectory()
        val cache = folder.resolve("cache").ensureDirectory()
        val file = upload.resolve("testfile.txt").toFile()
        file.createNewFile()
        val nonce = "StorageTest.shouldBeAbleToCacheResults.${randomUUID()}"
        val storage = aws.resultsStorage(nonce)
        storage.upload(upload.toFile())
        storage.download(download).toFile()

        // when
        val cacheNonce = "StorageTest.shouldBeAbleToCacheResults.${randomUUID()}"
        val cacheStorage = aws.resultsStorage(cacheNonce)
        cacheStorage.upload(download.toFile())
        val cachedResults = cacheStorage.download(cache).toFile()

        // then
        assertThat(cachedResults).exists()
        assertThat(cachedResults.list()).isEqualTo(upload.toFile().list())
        assertThat(cachedResults.list()).isEqualTo(download.toFile().list())
    }

    @Test
    fun shouldNotMixPrefixes(@TempDir folder: Path) {
        // given
        val uploadAlpha = folder.resolve("uploadAlpha").ensureDirectory()
        val uploadBeta = folder.resolve("uploadBeta").ensureDirectory()
        val downloadAlpha = folder.resolve("downloadAlpha").ensureDirectory()
        uploadAlpha.resolve("alpha.txt").toFile().also { it.createNewFile() }
        uploadBeta.resolve("beta.txt").toFile().also { it.createNewFile() }
        val nonce = "shouldNotMixPrefixes-${randomUUID()}"
        val storageAlpha = aws.resultsStorage("$nonce/sample-1")
        val storageBeta = aws.resultsStorage("$nonce/sample-10")

        // when
        storageAlpha.upload(uploadAlpha.toFile())
        storageBeta.upload(uploadBeta.toFile())
        storageAlpha.download(downloadAlpha).toFile()

        // then
        assertThat(downloadAlpha.toFile().list()).containsExactly("alpha.txt")
    }
}