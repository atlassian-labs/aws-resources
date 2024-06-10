package com.atlassian.performance.tools.aws.api

import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.DescribeImagesResult
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Image
import com.atlassian.performance.tools.aws.CanonicalOwnerIdRegistry
import com.atlassian.performance.tools.aws.FakeEc2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CanonicalImageIdByNameResolverTest {
    @Test
    fun queriesWithRegionSpecificOwnerIdAndImageNameForEveryRegion() {
        Regions.values().forEach { region ->
            var savedRequest: DescribeImagesRequest? = null
            val queriedImageName = "name-of-single-image"
            val expectedImageId = "id-of-single-image"
            val ec2 = object : AmazonEC2 by FakeEc2() {
                override fun describeImages(
                    describeImagesRequest: DescribeImagesRequest?
                ) = DescribeImagesResult()
                    .withImages(Image().withImageId(expectedImageId))
                    .also {
                        savedRequest = describeImagesRequest
                    }
            }
            val resolver = CanonicalImageIdByNameResolver.Builder(ec2)
                .region(region)
                .build()

            resolver.invoke(queriedImageName)

            assertThat(savedRequest).isNotNull
            assertThat(savedRequest!!.filters).contains(
                Filter("name", listOf(queriedImageName)),
                Filter("owner-id", listOf(CanonicalOwnerIdRegistry.forRegion(region)))
            )
        }
    }

    @Test
    fun queriesWithDefaultOwnerIdAndImageNameWhenNoRegionIsProvided() {
        var savedRequest: DescribeImagesRequest? = null
        val queriedImageName = "name-of-single-image"
        val expectedImageId = "id-of-single-image"
        val ec2 = object : AmazonEC2 by FakeEc2() {
            override fun describeImages(
                describeImagesRequest: DescribeImagesRequest?
            ) = DescribeImagesResult()
                .withImages(Image().withImageId(expectedImageId))
                .also {
                    savedRequest = describeImagesRequest
                }
        }
        val resolver = CanonicalImageIdByNameResolver.Builder(ec2)
            .build()

        resolver.invoke(queriedImageName)

        assertThat(savedRequest).isNotNull
        assertThat(savedRequest!!.filters).contains(
            Filter("name", listOf(queriedImageName)),
            Filter("owner-id", listOf("099720109477"))
        )
    }

    @Test
    fun returnsSingleIdReturnedByEc2() {
        val queriedImageName = "name-of-single-image"
        val expectedImageId = "id-of-single-image"
        val ec2 = object : AmazonEC2 by FakeEc2() {
            override fun describeImages(
                describeImagesRequest: DescribeImagesRequest?
            ) = DescribeImagesResult()
                .withImages(Image().withImageId(expectedImageId))
        }
        val resolver = CanonicalImageIdByNameResolver.Builder(ec2)
            .build()

        val result = resolver.invoke(queriedImageName)

        assertThat(result).isEqualTo(expectedImageId)
    }

    @Test
    fun failsWhenNoImageIsFound() {
        val queriedImageName = "name-of-single-image"
        val ec2 = object : AmazonEC2 by FakeEc2() {
            override fun describeImages(
                describeImagesRequest: DescribeImagesRequest?
            ) = DescribeImagesResult()
        }
        val resolver = CanonicalImageIdByNameResolver.Builder(ec2)
            .build()

        val result = try {
            resolver.invoke(queriedImageName)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(result).isNotNull()
    }

    @Test
    fun failsWhenMoreThanOneImageIsFound() {
        val queriedImageName = "name-of-single-image"
        val ec2 = object : AmazonEC2 by FakeEc2() {
            override fun describeImages(
                describeImagesRequest: DescribeImagesRequest?
            ) = DescribeImagesResult()
                .withImages(
                    Image().withImageId("id-of-image-1"),
                    Image().withImageId("id-of-image-2")
                )
        }
        val resolver = CanonicalImageIdByNameResolver.Builder(ec2)
            .build()

        val result = try {
            resolver.invoke(queriedImageName)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(result).isNotNull()
    }
}