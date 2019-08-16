package com.cameo.source.dao

import com.cameo.source.table.EpisodesTable
import com.cameo.source.table.SeasonsTable
import com.cameo.source.table.SeriesTable
import com.cameo.source.table.UsersScheduleSeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserScheduleSeriesEpisodesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserScheduleSeriesEpisodesDAO>(UsersScheduleSeriesTable)

    var seriesId by SeriesTable.id
    var seasonId by SeasonsTable.id
    var episodeId by EpisodesTable.id
    var seriesTitle by SeriesTable.title
    var seasonTitle by SeasonsTable.title
    var episodeTitle by EpisodesTable.title
    var airDate by EpisodesTable.airDate
    var backdrop by SeriesTable.backdropPath
}