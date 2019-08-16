package com.cameo.source.dao

import com.cameo.source.table.SeriesGenresTable
import com.cameo.source.table.SeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class OnboardingSeriesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OnboardingSeriesDAO>(SeriesTable)

    var title by SeriesTable.title
    var popularity by SeriesTable.popularity
    var posterPath by SeriesTable.posterPath
    val genres by SeriesGenreDAO referrersOn SeriesGenresTable.seriesId
}