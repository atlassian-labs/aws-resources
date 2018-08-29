package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.atlassian.performance.tools.io.api.copy
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

data class Storage(
    private val s3: AmazonS3,
    private val prefix: String,
    private val bucketName: String
) {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    val uri: URI = URI("s3://$bucketName/$prefix")
    val location = StorageLocation(uri, Regions.fromName(s3.regionName))

    fun upload(
        file: File
    ) {
        upload(
            file = file,
            bucketName = bucketName,
            key = "$prefix/${file.name}"
        )
    }

    private fun upload(
        file: File,
        bucketName: String,
        key: String
    ) {
        if (file.isDirectory) {
            Files.newDirectoryStream(file.toPath()).use {
                it.forEach {
                    upload(it.toFile(), bucketName, "$key/${it.fileName}")
                }
            }
        } else {
            logger.debug("Uploading $file to $bucketName under $key")
            s3.putObject(bucketName, key, file)
        }
    }

    fun download(
        rootTarget: Path
    ): Path {
        var token: String? = null
        do {
            val listing = s3.listObjectsV2(
                ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(prefix)
                    .withContinuationToken(token)
            )
            download(listing, rootTarget)
            token = listing.nextContinuationToken
        } while (token != null)
        return rootTarget.resolve(bucketName).resolve(prefix)
    }

    private fun download(
        listing: ListObjectsV2Result,
        rootTarget: Path
    ) {
        listing
            .objectSummaries
            .map { it.key }
            .forEach { key ->
                val leafTarget = rootTarget.resolve(bucketName).resolve(key)
                logger.debug("Downloading $bucketName/$key into $leafTarget")
                s3.getObject(bucketName, key).objectContent.use { stream ->
                    stream.copy(leafTarget)
                }
            }
    }
}
