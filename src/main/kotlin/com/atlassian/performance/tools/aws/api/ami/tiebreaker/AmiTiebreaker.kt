package com.atlassian.performance.tools.aws.api.ami.tiebreaker

import com.amazonaws.services.ec2.model.Image

interface AmiTiebreaker {
    fun pick(amis: List<Image>): Image?
}
