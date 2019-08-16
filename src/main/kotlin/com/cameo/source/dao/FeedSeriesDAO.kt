package com.cameo.source.dao

import com.cameo.source.table.SeasonsTable
import com.cameo.source.table.SeriesGenresTable
import com.cameo.source.table.SeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class FeedSeriesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FeedSeriesDAO>(SeriesTable)

    var title by SeriesTable.title
    var poster by SeriesTable.posterPath
    var overview by SeriesTable.overview
    var airDate by SeasonsTable.airDate
    val genres by SeriesGenreShortDAO referrersOn SeriesGenresTable.seriesId
}