package com.cameo.source.dao

import com.cameo.source.table.MovieGenresTable
import com.cameo.source.table.MoviesTable
import com.cameo.source.table.UsersScheduleMoviesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserScheduleMoviesInfoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserScheduleMoviesInfoDAO>(UsersScheduleMoviesTable)

    var releaseDate by MoviesTable.releaseDate
    var movieId by MoviesTable.id
    var movieTitle by MoviesTable.title
    var backdrop by MoviesTable.backdropPath
    var userId by UsersScheduleMoviesTable.userId
    val genres by MovieGenreShortDAO referrersOn MovieGenresTable.movieId
}