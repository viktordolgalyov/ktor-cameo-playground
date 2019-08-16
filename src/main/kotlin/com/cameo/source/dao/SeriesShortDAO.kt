package com.cameo.source.dao

import com.cameo.source.table.SeriesGenresTable
import com.cameo.source.table.SeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesShortDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesShortDAO>(SeriesTable)

    var title by SeriesTable.title
    var backdropPath by SeriesTable.backdropPath
    var posterPath by SeriesTable.posterPath
    val genres by SeriesGenreShortDAO referrersOn SeriesGenresTable.seriesId
}