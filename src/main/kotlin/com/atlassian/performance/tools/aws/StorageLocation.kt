package com.atlassian.performance.tools.aws

import com.amazonaws.regions.Regions
import java.net.URI

data class StorageLocation(
    val uri: URI,
    val region: Regions
) {
    val regionName: String = region.getName()

    fun toKotlinCodeSnippet(): String {
        return "${this::class.java.name}(${URI::class.java.name}(\"$uri\"), ${Regions::class.java.name}.$region)"
    }
}
