package com.atlassian.performance.tools.aws

import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Instance

/**
 * Scrolls through batches of AWS EC2 instances.
 */
interface ScrollingEc2 {
    fun scrollThroughInstances(
        vararg filters: Filter,
        batchAction: (List<Instance>) -> Unit
    )

    fun findInstances(
        vararg filters: Filter
    ): List<Instance>

    fun allocated(): Filter
}