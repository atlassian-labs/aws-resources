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
        .map { it.imageId }
        .let {
            when {
                it.isEmpty() -> throw Exception("Failed to find image $imageName in $region")
                it.size > 1 -> throw Exception("More than one image found with name $imageName in declared region $region. Selecting any of them automatically could create a security risk, so we can't proceed")
                else -> it.first()
            }
        }

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