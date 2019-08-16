package com.cameo.useCase.unfollowItem

import com.cameo.common.data.model.MediaType

data class UnfollowItemRequest(val itemId: Int, val type: MediaType)