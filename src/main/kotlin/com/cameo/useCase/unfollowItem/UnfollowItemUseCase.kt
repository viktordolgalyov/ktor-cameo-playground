package com.cameo.useCase.unfollowItem

import com.cameo.common.data.model.MediaType
import com.cameo.common.requestThread
import com.cameo.source.table.MoviesTable
import com.cameo.source.table.SeriesTable
import com.cameo.source.table.UsersScheduleMoviesTable
import com.cameo.source.table.UsersScheduleSeriesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select

class UnfollowItemUseCase {

    suspend fun execute(userId: Int, request: UnfollowItemRequest) = requestThread {
        when (request.type) {
            MediaType.Movie -> deleteMovie(userId, request.itemId)
            MediaType.Series -> deleteSeries(userId, request.itemId)
        }
    }

    private fun deleteMovie(userId: Int, itemId: Int) {
        val movieEntityId = MoviesTable
                .slice(MoviesTable.id)
                .select { MoviesTable.id eq itemId }
                .limit(1)
                .map { it[MoviesTable.id] }
                .firstOrNull()
        movieEntityId?.let {
            UsersScheduleMoviesTable
                    .deleteWhere {
                        UsersScheduleMoviesTable.userId.eq(userId) and
                                (UsersScheduleMoviesTable.movieId eq movieEntityId)
                    }
        }
    }

    private fun deleteSeries(userId: Int, itemId: Int) {
        val seriesEntityId = SeriesTable
                .slice(SeriesTable.id)
                .select { SeriesTable.id eq itemId }
                .limit(1)
                .map { it[SeriesTable.id] }
                .firstOrNull()
        seriesEntityId?.let {
            UsersScheduleSeriesTable
                    .deleteWhere {
                        UsersScheduleSeriesTable.userId.eq(userId) and
                                (UsersScheduleSeriesTable.seriesId eq seriesEntityId)
                    }
        }
    }
}