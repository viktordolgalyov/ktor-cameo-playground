package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object UsersScheduleMoviesTable : IntIdTable() {
    val userId = reference("user_id", UsersTable).index()
    val movieId = reference("movie_id", MoviesTable).index()
    val subscribeTimestamp = long("subscribe_timestamp")
}