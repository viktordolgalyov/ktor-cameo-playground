package com.cameo.source.dao

import com.cameo.source.table.MovieGenresTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class MovieGenreDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieGenreDAO>(MovieGenresTable)

    var genreId by MovieGenresTable.genreId
    var movie by MovieShortDAO referencedOn MovieGenresTable.movieId
}