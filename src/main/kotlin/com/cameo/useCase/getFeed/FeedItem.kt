package com.cameo.useCase.getFeed

import com.cameo.common.data.model.MediaType

data class FeedItem(val id: Int,
                    val title: String,
                    val poster: String,
                    val genres: String,
                    val shortOverview: String,
                    val releaseDate: String,
                    val type: MediaType)