package com.cameo.useCase.getSearchItems

import com.cameo.common.data.model.MediaType

data class SearchItem(val id: Int,
                      val title: String,
                      val poster: String,
                      val popularity: Float,
                      val genre: String,
                      val type: MediaType)