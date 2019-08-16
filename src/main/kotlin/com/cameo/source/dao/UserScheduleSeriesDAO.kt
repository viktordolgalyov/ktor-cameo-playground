package com.cameo.source.dao

import com.cameo.source.table.SeriesGenresTable
import com.cameo.source.table.SeriesTable
import com.cameo.source.table.UsersScheduleSeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserScheduleSeriesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserScheduleSeriesDAO>(UsersScheduleSeriesTable)

    var userId by UsersScheduleSeriesTable.userId
    var subscribeTimestamp by UsersScheduleSeriesTable.subscribeTimestamp
    var seriesId by SeriesTable.id
    var seriesTitle by SeriesTable.title
    var seriesPoster by SeriesTable.posterPath
    val genres by SeriesGenreShortDAO referrersOn SeriesGenresTable.seriesId
}