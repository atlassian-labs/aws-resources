package com.atlassian.performance.tools.aws.api

import com.amazonaws.regions.Regions

class TextCapacityMediator private constructor(
    private val regionDescription: String
) : CapacityMediator {

    @Deprecated(
        message = "Using this constructor does not provide user with information about AWS Region",
        replaceWith = ReplaceWith("TextCapacityMediator(region: Regions)")
    )
    constructor() : this("your current region")
    constructor(region: Regions) : this(region.getName())

    override fun bump(
        limitType: String,
        desiredLimit: () -> Int
    ): String {
        return "You either need to bump $limitType to ${desiredLimit()} in $regionDescription" +
            " manually or inject SupportCapacityMediator into Aws for automatic management"
    }
}
