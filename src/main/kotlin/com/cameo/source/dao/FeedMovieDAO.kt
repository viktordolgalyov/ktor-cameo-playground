package com.cameo.source.dao

import com.cameo.source.table.MovieGenresTable
import com.cameo.source.table.MoviesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class FeedMovieDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FeedMovieDAO>(MoviesTable)

    var title by MoviesTable.title
    var poster by MoviesTable.posterPath
    var overview by MoviesTable.overview
    var releaseDate by MoviesTable.releaseDate
    val genres by MovieGenreShortDAO referrersOn MovieGenresTable.movieId
}