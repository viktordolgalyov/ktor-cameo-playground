package com.cameo.source.dao

import com.cameo.source.table.MovieTrailersTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class MovieTrailerDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieTrailerDAO>(MovieTrailersTable)

    var movie by MovieShortDAO referencedOn MovieTrailersTable.movieId
    var trailerPath by MovieTrailersTable.trailerPath
}