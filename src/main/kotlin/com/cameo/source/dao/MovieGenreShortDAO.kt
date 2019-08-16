package com.cameo.source.dao

import com.cameo.source.table.MovieGenresTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class MovieGenreShortDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieGenreShortDAO>(MovieGenresTable)

    var genreId by MovieGenresTable.genreId
}