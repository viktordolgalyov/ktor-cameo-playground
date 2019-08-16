package com.cameo.useCase.getUserList

import com.cameo.common.asImageUrl
import com.cameo.common.data.PaginationInfo
import com.cameo.common.data.getGenre
import com.cameo.common.requestThread
import com.cameo.source.dao.UserScheduleMovieDAO
import com.cameo.source.dao.UserScheduleSeriesDAO
import com.cameo.source.table.*
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select

class GetUserListUseCase {

    suspend fun execute(userId: Int, pagination: PaginationInfo): List<UserMediaItem> {
        return requestThread {
            (series(userId, pagination) + movies(userId, pagination))
                    .sortedByDescending { it.first }
                    .map { it.second }
        }
    }

    private fun movies(userId: Int, pagination: PaginationInfo): List<Pair<Long, UserMediaItem>> {
        val query = UsersScheduleMoviesTable
                .join(MoviesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleMoviesTable.movieId eq MoviesTable.id
                })
                .join(MovieGenresTable, JoinType.LEFT, additionalConstraint = {
                    MoviesTable.id eq MovieGenresTable.movieId
                })
                .slice(MoviesTable.id, MoviesTable.title, MoviesTable.posterPath,
                        MovieGenresTable.id, MovieGenresTable.genreId,
                        UsersScheduleMoviesTable.id, UsersScheduleMoviesTable.userId, UsersScheduleMoviesTable.subscribeTimestamp)
                .select { UsersScheduleMoviesTable.userId.eq(userId) }

        return UserScheduleMovieDAO
                .wrapRows(query)
                .limit(pagination.itemsPerPage, pagination.itemsPerPage * pagination.page)
                .distinctBy { it.movieId }
                .map {
                    val movie = UserMovie(
                            it.movieId.value,
                            it.movieTitle,
                            it.moviePoster.asImageUrl(),
                            it.genres.mapNotNull { it.genreId?.let { getGenre(it) } })
                    it.subscribeTimestamp to UserMediaItem(movie = movie)
                }
    }

    private fun series(userId: Int, pagination: PaginationInfo): List<Pair<Long, UserMediaItem>> {
        val query = UsersScheduleSeriesTable
                .join(SeriesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleSeriesTable.seriesId eq SeriesTable.id
                })
                .join(SeriesGenresTable, JoinType.LEFT, additionalConstraint = {
                    SeriesTable.id eq SeriesGenresTable.seriesId
                })
                .slice(SeriesTable.id, SeriesTable.title, SeriesTable.posterPath,
                        SeriesGenresTable.id, SeriesGenresTable.genreId, SeriesGenresTable.seriesId,
                        UsersScheduleSeriesTable.id, UsersScheduleSeriesTable.userId, UsersScheduleSeriesTable.subscribeTimestamp)
                .select { UsersScheduleSeriesTable.userId.eq(userId) }

        return UserScheduleSeriesDAO
                .wrapRows(query)
                .limit(pagination.itemsPerPage, pagination.itemsPerPage * pagination.page)
                .distinctBy { it.seriesId }
                .map {
                    val series = UserSeries(
                            it.seriesId.value,
                            it.seriesTitle,
                            it.seriesPoster.asImageUrl(),
                            it.genres.mapNotNull { it.genreId?.let { getGenre(it) } })
                    it.subscribeTimestamp to UserMediaItem(series = series)
                }
    }
}