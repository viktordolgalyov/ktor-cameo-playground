package com.cameo.source.dao

import com.cameo.source.table.MovieGenresTable
import com.cameo.source.table.MoviesTable
import com.cameo.source.table.UsersScheduleMoviesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserScheduleMovieDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserScheduleMovieDAO>(UsersScheduleMoviesTable)

    var userId by UsersScheduleMoviesTable.userId
    var subscribeTimestamp by UsersScheduleMoviesTable.subscribeTimestamp
    var movieId by MoviesTable.id
    var movieTitle by MoviesTable.title
    var moviePoster by MoviesTable.posterPath
    val genres by MovieGenreShortDAO referrersOn MovieGenresTable.movieId
}