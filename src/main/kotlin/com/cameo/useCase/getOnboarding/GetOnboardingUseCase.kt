package com.cameo.useCase.getOnboarding

import com.cameo.common.asImageUrl
import com.cameo.common.data.PaginationInfo
import com.cameo.common.data.getGenre
import com.cameo.common.requestThread
import com.cameo.source.dao.OnboardingMovieDAO
import com.cameo.source.dao.OnboardingSeriesDAO

class GetOnboardingUseCase {

    suspend fun execute(userId: Int, pagination: PaginationInfo): List<OnboardingItem> {
        return requestThread {
            (readMovies(pagination) + readSeries(pagination))
                    .sortedByDescending { it.first }
                    .map { it.second }
        }
    }

    private fun readMovies(pagination: PaginationInfo): List<Pair<Float, OnboardingItem>> {
        return OnboardingMovieDAO
                .all()
                .limit(pagination.itemsPerPage, pagination.itemsPerPage * pagination.page)
                .sortedByDescending { it.popularity }
                .map {
                    val movie = OnboardingMovieItem(
                            it.id.value,
                            it.title,
                            it.genres.firstOrNull()?.genreId?.let { getGenre(it)?.name }.orEmpty(),
                            it.poster.asImageUrl())
                    it.popularity to OnboardingItem(movie = movie, series = null)
                }
    }

    private fun readSeries(pagination: PaginationInfo): List<Pair<Float, OnboardingItem>> {
        return OnboardingSeriesDAO
                .all()
                .limit(pagination.itemsPerPage, pagination.itemsPerPage * pagination.page)
                .sortedByDescending { it.popularity }
                .map {
                    val series = OnboardingSeriesItem(
                            it.id.value,
                            it.title,
                            it.genres.firstOrNull()?.genreId?.let { getGenre(it)?.name }.orEmpty(),
                            it.posterPath.asImageUrl()
                    )
                    it.popularity to OnboardingItem(movie = null, series = series)
                }
    }
}