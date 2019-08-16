package com.cameo.useCase.followItem

import com.cameo.common.data.model.MediaType

data class FollowItemRequest(val itemId: Int, val itemType: MediaType)