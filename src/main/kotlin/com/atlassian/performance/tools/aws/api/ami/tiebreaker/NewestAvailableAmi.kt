package com.atlassian.performance.tools.aws.api.ami.tiebreaker

import com.amazonaws.services.ec2.model.Image
import com.amazonaws.services.ec2.model.ImageState

class NewestAvailableAmi : AmiTiebreaker {
    override fun pick(amis: List<Image>): Image? {
        return amis
            .asSequence()
            .filter { ImageState.fromValue(it.state) == ImageState.Available }
            .sortedByDescending { it.creationDate + it.imageId }
            .firstOrNull()
    }
}
