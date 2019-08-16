package com.cameo.useCase.getUserList

import com.cameo.common.data.model.LocalGenre


data class UserMediaItem(val movie: UserMovie? = null,
                         val series: UserSeries? = null)

data class UserMovie(val id: Int,
                     val title: String,
                     val poster: String,
                     val genres: List<LocalGenre>)

data class UserSeries(val id: Int,
                      val title: String,
                      val poster: String,
                      val genres: List<LocalGenre>)