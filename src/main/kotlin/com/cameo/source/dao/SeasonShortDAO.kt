package com.cameo.source.dao

import com.cameo.source.table.EpisodesTable
import com.cameo.source.table.SeasonsTable
import com.cameo.source.table.SeriesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeasonShortDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeasonShortDAO>(SeasonsTable)

    var seasonNumber by SeasonsTable.seasonNumber
    var title by SeasonsTable.title
    var series by SeriesShortDAO referencedOn SeasonsTable.seriesId
    val episodes by EpisodeShortDAO referrersOn EpisodesTable.seasonId
}