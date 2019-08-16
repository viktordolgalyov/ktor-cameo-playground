package com.cameo.source.dao

import com.cameo.source.table.SeriesGenresTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesGenreDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesGenreDAO>(SeriesGenresTable)

    var series by SeriesShortDAO referencedOn SeriesGenresTable.seriesId
    var genreId by SeriesGenresTable.genreId
}