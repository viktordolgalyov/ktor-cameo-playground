package com.cameo.useCase.getSeriesDetails

import com.cameo.common.data.model.PersonListItem

data class SeriesDetails(val id: Int,
                         val title: String,
                         val overview: String,
                         val year: String,
                         val genre: String,
                         val seasonCount: Int,
                         val status: String,
                         val posterUrl: String,
                         val cast: List<PersonListItem>,
                         val crew: List<PersonListItem>,
                         val isFollowed: Boolean)