package com.atlassian.performance.tools.aws.api

import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.Filter
import com.atlassian.performance.tools.aws.CanonicalOwnerIdRegistry

class CanonicalImageIdByNameResolver private constructor(
    private val ec2: AmazonEC2,
    private val region: Regions?
): (String) -> String {

    override fun invoke(
        imageName: String
    ): String = ec2
        .describeImages(
            DescribeImagesRequest().withFilters(
                Filter("name", listOf(imageName)),
                Filter("owner-id", listOf(CanonicalOwnerIdRegistry.forRegion(region)))
            )
        )
        .images
        .sortedByDescending { it.creationDate }
        .map { it.imageId }
        .firstOrNull()
        ?: throw Exception("Failed to find image containing $imageName in $region")

    class Builder(
        private val ec2: AmazonEC2
    ) {
        private var region: Regions? = null

        fun region(region: Regions) = apply { this.region = region }

        fun build() = CanonicalImageIdByNameResolver(
            ec2 = ec2,
            region = region
        )
    }
}