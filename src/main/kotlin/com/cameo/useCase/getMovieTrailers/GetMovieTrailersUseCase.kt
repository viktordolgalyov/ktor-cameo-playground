package com.cameo.useCase.getMovieTrailers

import com.cameo.common.asYoutubeImage
import com.cameo.common.asYoutubeVideo
import com.cameo.common.data.model.Video
import com.cameo.common.requestThread
import com.cameo.source.dao.MovieTrailerDAO
import com.cameo.source.table.MovieTrailersTable

class GetMovieTrailersUseCase {

    suspend fun execute(movieId: Int, userId: Int) = requestThread {
        MovieTrailerDAO
                .find { MovieTrailersTable.movieId eq movieId }
                .map { Video(it.trailerPath.asYoutubeImage(), it.trailerPath.asYoutubeVideo()) }
    }
}