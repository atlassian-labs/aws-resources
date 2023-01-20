package com.atlassian.performance.tools.aws.api.ami.tiebreaker

import com.amazonaws.services.ec2.model.Image
import com.amazonaws.services.ec2.model.ImageState
import com.amazonaws.services.ec2.model.ImageState.Available
import com.amazonaws.services.ec2.model.ImageState.Pending

class NewestAmiTiebreaker : AmiTiebreaker {
    override fun pick(amis: List<Image>): Image? {
        return amis
            .asSequence()
            .filter { ImageState.fromValue(it.state) in listOf(Available, Pending) }
            .sortedByDescending { it.creationDate }
            .firstOrNull()
    }
}
