package com.cameo.source.dao

import com.cameo.source.table.SeriesGenresTable
import com.cameo.source.table.SeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SearchSeriesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchSeriesDAO>(SeriesTable)

    var title by SeriesTable.title
    var posterPath by SeriesTable.posterPath
    var popularity by SeriesTable.popularity
    val genres by SearchSeriesGenreDAO referrersOn SeriesGenresTable.seriesId
}

class SearchSeriesGenreDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchSeriesGenreDAO>(SeriesGenresTable)

    var series by SearchSeriesDAO referencedOn SeriesGenresTable.seriesId
    var genreId by SeriesGenresTable.genreId
}