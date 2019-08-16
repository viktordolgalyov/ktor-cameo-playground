package com.cameo.useCase.getSchedule

import com.cameo.common.data.model.MediaType

data class ScheduleItem(val movie: ScheduleMovie?,
                        val series: ScheduleSeries?,
                        val type: MediaType)

data class ScheduleSeries(val seriesId: Int,
                          val seasonId: Int,
                          val episodeId: Int,
                          val seriesTitle: String,
                          val seasonTitle: String,
                          val episodeTitle: String,
                          val releaseDate: String,
                          val backdrop: String)

data class ScheduleMovie(val id: Int,
                         val releaseDate: String,
                         val title: String,
                         val genres: String,
                         val backdrop: String)