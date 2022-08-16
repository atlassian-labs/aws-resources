package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2
import com.atlassian.performance.tools.aws.api.CanonicalImageIdByNameResolver

internal class AwsDefaultAmiIdProvider(
    ec2: AmazonEC2,
    region: Regions
) : () -> String {
    private val imageIdByNameResolver = CanonicalImageIdByNameResolver.Builder(ec2)
        .region(region)
        .build()

    override fun invoke() = imageIdByNameResolver(DefaultAmiNameRegistry.hvmSsdUbuntuXenial16_04Amd64Server20180912)
}