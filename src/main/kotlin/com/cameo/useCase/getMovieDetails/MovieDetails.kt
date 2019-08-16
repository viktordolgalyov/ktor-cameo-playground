package com.cameo.useCase.getMovieDetails

import com.cameo.common.data.model.PersonListItem
import com.cameo.common.data.model.Video

data class MovieDetails(val id: Int,
                        val title: String,
                        val overview: String,
                        val poster: String,
                        val backdrop: String,
                        val tagline: String,
                        val status: String,
                        val releaseDate: String,
                        val budget: Long,
                        val revenue: Long,
                        val homepage: String,
                        val runtime: Int,
                        val trailers: List<Video>,
                        val genres: List<String>,
                        val cast: List<PersonListItem>,
                        val crew: List<PersonListItem>,
                        val isFollowed: Boolean)