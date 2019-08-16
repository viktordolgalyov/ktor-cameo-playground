package com.cameo.useCase.followItem

import com.cameo.common.data.model.MediaType
import com.cameo.common.requestThread
import com.cameo.source.table.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class FollowItemUseCase {

    suspend fun execute(userId: Int, request: FollowItemRequest) = requestThread {
        when (request.itemType) {
            MediaType.Movie -> addMovieToSchedule(userId, request.itemId)
            MediaType.Series -> addSeriesToSchedule(userId, request.itemId)
        }
    }

    private fun addMovieToSchedule(userId: Int, itemId: Int) {
        val movieEntityId = MoviesTable
                .slice(MoviesTable.id)
                .select { MoviesTable.id eq itemId }
                .limit(1)
                .map { it[MoviesTable.id] }
                .firstOrNull()
        val userEntityId = UsersTable
                .slice(UsersTable.id)
                .select { UsersTable.id eq userId }
                .limit(1)
                .map { it[UsersTable.id] }
                .firstOrNull()
        userEntityId?.let {
            movieEntityId?.let {
                UsersScheduleMoviesTable.insert {
                    it[UsersScheduleMoviesTable.userId] = userEntityId
                    it[UsersScheduleMoviesTable.movieId] = movieEntityId
                    it[UsersScheduleMoviesTable.subscribeTimestamp] = System.currentTimeMillis() / 1000
                }
            }
        } ?: throw IllegalArgumentException()
    }

    private fun addSeriesToSchedule(userId: Int, itemId: Int) {
        val seriesEntityId = SeriesTable
                .slice(SeriesTable.id)
                .select { SeriesTable.id eq itemId }
                .limit(1)
                .map { it[SeriesTable.id] }
                .firstOrNull()
        val userEntityId = UsersTable
                .slice(UsersTable.id)
                .select { UsersTable.id eq userId }
                .limit(1)
                .map { it[UsersTable.id] }
                .firstOrNull()
        userEntityId?.let {
            seriesEntityId?.let {
                UsersScheduleSeriesTable.insert {
                    it[UsersScheduleSeriesTable.userId] = userEntityId
                    it[UsersScheduleSeriesTable.seriesId] = seriesEntityId
                    it[UsersScheduleSeriesTable.subscribeTimestamp] = System.currentTimeMillis() / 1000
                }
            } ?: throw IllegalArgumentException()
        } ?: throw IllegalArgumentException()
    }
}