package com.cameo.source.dao

import com.cameo.source.table.SeriesGenresTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesGenreShortDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesGenreShortDAO>(SeriesGenresTable)

    var genreId by SeriesGenresTable.genreId
}