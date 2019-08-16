package com.cameo.source.dao

import com.cameo.source.table.MovieGenresTable
import com.cameo.source.table.MoviesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class OnboardingMovieDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OnboardingMovieDAO>(MoviesTable)

    var title by MoviesTable.title
    var poster by MoviesTable.posterPath
    var popularity by MoviesTable.popularity
    val genres by MovieGenreDAO referrersOn MovieGenresTable.movieId
}