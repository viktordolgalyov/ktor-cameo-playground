package com.cameo.useCase.getFeed

import com.cameo.common.asImageUrl
import com.cameo.common.data.PaginationInfo
import com.cameo.common.data.fromEpoch
import com.cameo.common.data.getGenre
import com.cameo.common.data.model.MediaType
import com.cameo.common.data.toEpoch
import com.cameo.common.requestThread
import com.cameo.source.table.*
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime

class GetFeedUseCase {

    suspend fun execute(userId: Int, pagination: PaginationInfo): List<FeedItem> {
        return requestThread {
            val scheduleStart = DateTime.now().minusWeeks(2).withMillisOfDay(0)
            val scheduleEnd = DateTime.now().plusYears(1).withMillisOfDay(0)
            val fromDate = scheduleStart.plusMonths(pagination.page)
            val toDate = fromDate.plusMonths(1)
            val from = fromDate.withMillisOfDay(0).toEpoch()
            val to = (if (toDate.isAfter(scheduleEnd)) scheduleEnd else toDate).withMillisOfDay(0).toEpoch()

            (movies(from, to) + series(from, to))
                    .filterNot {
                        it.poster.endsWith("original/") ||
                                it.poster.isBlank() ||
                                it.genres.isEmpty()
                    }
                    .sortedBy { it.releaseDate.toEpoch() }
        }
    }

    private fun movies(from: Long, to: Long): List<FeedItem> {
        val query = MoviesTable
                .join(MovieGenresTable, JoinType.LEFT, additionalConstraint = {
                    MovieGenresTable.movieId eq MoviesTable.id
                })
                .slice(MoviesTable.id, MoviesTable.title, MoviesTable.posterPath, MoviesTable.overview,
                        MoviesTable.releaseDate, MoviesTable.popularity, MoviesTable.originalLanguage,
                        MovieGenresTable.genreId)
                .select {
                    MoviesTable.releaseDate.between(from, to) and
                            (MoviesTable.popularity greaterEq 15.0f) and
                            (MoviesTable.overview.isNotNull() and MoviesTable.overview.neq(""))
                }
        return query
                .map { it[MoviesTable.id] to it }
                .filter { it.second[MoviesTable.originalLanguage] in arrayOf("en", "de", "fr", "ru", "nl", "sv") }
                .groupBy { it.first }
                .map {
                    val movie = it.value.first().second
                    val id = movie[MoviesTable.id]
                    val title = movie[MoviesTable.title]
                    val poster = movie[MoviesTable.posterPath].asImageUrl()
                    val overview = movie[MoviesTable.overview].take(SHORT_OVERVIEW_LENGTH)
                    val releaseDate = movie[MoviesTable.releaseDate].fromEpoch()
                    val genres = it.value.map { it.second[MovieGenresTable.genreId] }
                            .mapNotNull { it?.let { getGenre(it) }?.name }.joinToString()
                    FeedItem(id.value,
                            title,
                            poster,
                            genres,
                            overview,
                            releaseDate,
                            MediaType.Movie)
                }
                .distinctBy { it.id }
    }

    private fun series(from: Long, to: Long): List<FeedItem> {
        val query = SeriesTable
                .join(SeasonsTable, JoinType.LEFT, additionalConstraint = {
                    SeasonsTable.seriesId eq SeriesTable.id
                })
                .join(SeriesGenresTable, JoinType.LEFT, additionalConstraint = {
                    SeriesGenresTable.seriesId eq SeriesTable.id
                })
                .slice(SeriesTable.id, SeriesTable.title, SeriesTable.posterPath, SeasonsTable.airDate,
                        SeriesTable.overview, SeriesTable.popularity,
                        SeriesGenresTable.genreId)
                .select {
                    (SeasonsTable.airDate.between(from, to)) and
                            (SeriesTable.popularity greaterEq 15.0f) and
                            (SeriesTable.overview.isNotNull() and SeriesTable.overview.neq(""))
                }
        return query
                .map { it[SeriesTable.id] to it }
                .groupBy { it.first }
                .map {
                    val series = it.value.first().second
                    val id = series[SeriesTable.id]
                    val title = series[SeriesTable.title]
                    val poster = series[SeriesTable.posterPath].asImageUrl()
                    val overview = series[SeriesTable.overview].take(SHORT_OVERVIEW_LENGTH)
                    val airDate = series[SeasonsTable.airDate].fromEpoch()
                    val genres = it.value.map { it.second[SeriesGenresTable.genreId] }
                            .mapNotNull { it?.let { getGenre(it) }?.name }.joinToString()
                    FeedItem(id.value,
                            title,
                            poster,
                            genres,
                            overview,
                            airDate,
                            MediaType.Series)
                }
                .distinctBy { it.id }
    }
}

private const val SHORT_OVERVIEW_LENGTH = 200