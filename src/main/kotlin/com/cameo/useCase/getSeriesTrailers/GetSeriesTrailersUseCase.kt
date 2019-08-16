package com.cameo.useCase.getSeriesTrailers

import com.cameo.common.asYoutubeImage
import com.cameo.common.asYoutubeVideo
import com.cameo.common.data.model.Video
import com.cameo.common.requestThread
import com.cameo.source.dao.SeriesTrailerDAO
import com.cameo.source.table.SeriesTrailersTable

class GetSeriesTrailersUseCase {

    suspend fun execute(seriesId: Int, userId: Int) = requestThread {
        SeriesTrailerDAO
                .find { SeriesTrailersTable.seriesId eq seriesId }
                .map { Video(it.trailerPath.asYoutubeImage(), it.trailerPath.asYoutubeVideo()) }
    }
}