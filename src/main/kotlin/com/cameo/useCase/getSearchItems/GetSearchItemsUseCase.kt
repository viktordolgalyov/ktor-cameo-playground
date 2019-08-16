package com.cameo.useCase.getSearchItems

import com.cameo.common.asImageUrl
import com.cameo.common.data.PaginationInfo
import com.cameo.common.data.getGenre
import com.cameo.common.data.model.MediaType
import com.cameo.common.requestThread
import com.cameo.source.dao.SearchMovieDAO
import com.cameo.source.dao.SearchSeriesDAO
import com.cameo.source.table.MoviesTable
import com.cameo.source.table.SeriesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.upperCase

class GetSearchItemsUseCase {

    suspend fun execute(userId: Int, query: String, pagination: PaginationInfo): List<SearchItem> {
        return requestThread {
            (searchMovies(query, pagination) + searchSeries(query, pagination))
                    .sortedByDescending { it.popularity }
        }
    }

    private fun searchMovies(query: String, pagination: PaginationInfo): List<SearchItem> = SearchMovieDAO
            .find {
                ((MoviesTable.title.upperCase() like "%${query.toUpperCase()}") or
                        (MoviesTable.title.upperCase() like "${query.toUpperCase()}%")) and
                        (MoviesTable.popularity greaterEq 3f)
            }
            .limit(pagination.itemsPerPage, pagination.itemsPerPage * pagination.page)
            .map {
                SearchItem(
                        id = it.id.value,
                        title = it.title,
                        poster = it.poster.asImageUrl(),
                        popularity = it.popularity,
                        genre = it.genres.firstOrNull()?.genreId?.let { getGenre(it)?.name }.orEmpty(),
                        type = MediaType.Movie)
            }

    private fun searchSeries(query: String, pagination: PaginationInfo): List<SearchItem> = SearchSeriesDAO
            .find {
                ((SeriesTable.title.upperCase() like "%${query.toUpperCase()}") or
                        (SeriesTable.title.upperCase() like "${query.toUpperCase()}%")) and
                        (SeriesTable.popularity greaterEq 3f)
            }
            .limit(pagination.itemsPerPage, pagination.itemsPerPage * pagination.page)
            .map {
                SearchItem(
                        id = it.id.value,
                        title = it.title,
                        poster = it.posterPath.asImageUrl(),
                        popularity = it.popularity,
                        genre = it.genres.firstOrNull()?.genreId?.let { getGenre(it)?.name }.orEmpty(),
                        type = MediaType.Series)
            }
}