package com.cameo.useCase.getSeasons

data class SeasonsResponse(val seasons: List<SeasonInfo>)

data class SeasonInfo(val seasonId: Int,
                      val seriesId: Int,
                      val title: String,
                      val number: Int,
                      val episodes: List<EpisodeInfo>)

data class EpisodeInfo(val id: Int,
                       val seasonId: Int,
                       val seasonTitle: String,
                       val episodeTitle: String,
                       val episodeNumber: Int,
                       val airDate: String,
                       val imageUrl: String,
                       val isWatched: Boolean)