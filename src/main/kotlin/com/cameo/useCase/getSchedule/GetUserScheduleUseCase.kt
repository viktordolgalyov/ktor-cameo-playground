package com.cameo.useCase.getSchedule

import com.cameo.common.asImageUrl
import com.cameo.common.data.fromEpoch
import com.cameo.common.data.getGenre
import com.cameo.common.data.model.MediaType
import com.cameo.common.requestThread
import com.cameo.source.table.*
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

class GetUserScheduleUseCase {

    suspend fun execute(userId: Int, from: Long, to: Long): UserScheduleResponse = requestThread {
        val items = series(userId, from, to) + movies(userId, from, to)
        UserScheduleResponse(
                items,
                getMinScheduleDate(userId).fromEpoch(),
                getMaxScheduleDate(userId).fromEpoch())
    }

    private fun movies(userId: Int, from: Long, to: Long): List<ScheduleItem> {
        val query = MoviesTable
                .join(MovieGenresTable, JoinType.LEFT, additionalConstraint = {
                    MovieGenresTable.movieId eq MoviesTable.id
                })
                .join(UsersScheduleMoviesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleMoviesTable.movieId eq MoviesTable.id
                })
                .slice(MoviesTable.id, MoviesTable.title, MoviesTable.releaseDate,
                        MoviesTable.backdropPath, MovieGenresTable.genreId)
                .select {
                    UsersScheduleMoviesTable.userId eq userId and (MoviesTable.releaseDate.between(from, to))
                }
        return query
                .map { it[MoviesTable.id] to it }
                .groupBy { it.first }
                .map {
                    val movie = it.value.first().second
                    val id = movie[MoviesTable.id].value
                    val title = movie[MoviesTable.title]
                    val backdrop = movie[MoviesTable.backdropPath].asImageUrl()
                    val releaseDate = movie[MoviesTable.releaseDate].fromEpoch()
                    val genres = it.value.map { it.second[MovieGenresTable.genreId] }
                            .mapNotNull { it?.let { getGenre(it) }?.name }.joinToString()
                    val movieItem = ScheduleMovie(id, releaseDate, title, genres, backdrop)
                    ScheduleItem(movieItem, null, MediaType.Movie)
                }
                .distinctBy { it.movie?.id }
    }

    private fun series(userId: Int, from: Long, to: Long): List<ScheduleItem> {
        val query = EpisodesTable
                .join(SeasonsTable, JoinType.LEFT, additionalConstraint = {
                    EpisodesTable.seasonId eq SeasonsTable.id
                })
                .join(SeriesTable, JoinType.LEFT, additionalConstraint = {
                    SeasonsTable.seriesId eq SeriesTable.id
                })
                .join(UsersScheduleSeriesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleSeriesTable.seriesId eq SeriesTable.id
                })
                .slice(SeriesTable.id, SeasonsTable.id, EpisodesTable.id,
                        SeriesTable.title, SeasonsTable.title, EpisodesTable.title,
                        SeriesTable.backdropPath, EpisodesTable.airDate)
                .select {
                    UsersScheduleSeriesTable.userId eq userId and (EpisodesTable.airDate.between(from, to))
                }
        return query
                .map { it[SeriesTable.id] to it }
                .groupBy { it.first }
                .map {
                    val series = it.value.first().second
                    val id = series[SeriesTable.id].value
                    val seasonId = series[SeasonsTable.id].value
                    val episodeId = series[EpisodesTable.id].value
                    val seriesTitle = series[SeriesTable.title]
                    val seasonTitle = series[SeasonsTable.title]
                    val episodeTitle = series[EpisodesTable.title]
                    val airDate = series[EpisodesTable.airDate].fromEpoch()
                    val backdrop = series[SeriesTable.backdropPath].asImageUrl()
                    val seriesItem = ScheduleSeries(id, seasonId, episodeId, seriesTitle, seasonTitle, episodeTitle, airDate, backdrop)
                    ScheduleItem(null, seriesItem, MediaType.Series)
                }
                .distinctBy { it.series?.episodeId }
    }

    private fun getMinScheduleDate(userId: Int): Long {
        val minMoviesDate = MoviesTable
                .join(UsersScheduleMoviesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleMoviesTable.movieId eq MoviesTable.id
                })
                .slice(MoviesTable.releaseDate)
                .select { UsersScheduleMoviesTable.userId eq userId }
                .map { it[MoviesTable.releaseDate] }
                .min() ?: 0
        val minSeriesDate = EpisodesTable
                .join(SeasonsTable, JoinType.LEFT, additionalConstraint = {
                    EpisodesTable.seasonId eq SeasonsTable.id
                })
                .join(SeriesTable, JoinType.LEFT, additionalConstraint = {
                    SeasonsTable.seriesId eq SeriesTable.id
                })
                .join(UsersScheduleSeriesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleSeriesTable.seriesId eq SeriesTable.id
                })
                .slice(EpisodesTable.airDate)
                .select { UsersScheduleSeriesTable.userId eq userId }
                .map { it[EpisodesTable.airDate] }
                .min() ?: 0
        return Math.min(minMoviesDate, minSeriesDate)
    }

    private fun getMaxScheduleDate(userId: Int): Long {
        val minMoviesDate = MoviesTable
                .join(UsersScheduleMoviesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleMoviesTable.movieId eq MoviesTable.id
                })
                .slice(MoviesTable.releaseDate)
                .select { UsersScheduleMoviesTable.userId eq userId }
                .map { it[MoviesTable.releaseDate] }
                .max() ?: 0
        val minSeriesDate = EpisodesTable
                .join(SeasonsTable, JoinType.LEFT, additionalConstraint = {
                    EpisodesTable.seasonId eq SeasonsTable.id
                })
                .join(SeriesTable, JoinType.LEFT, additionalConstraint = {
                    SeasonsTable.seriesId eq SeriesTable.id
                })
                .join(UsersScheduleSeriesTable, JoinType.LEFT, additionalConstraint = {
                    UsersScheduleSeriesTable.seriesId eq SeriesTable.id
                })
                .slice(EpisodesTable.airDate)
                .select { UsersScheduleSeriesTable.userId eq userId }
                .map { it[EpisodesTable.airDate] }
                .max() ?: 0
        return Math.max(minMoviesDate, minSeriesDate)
    }
}