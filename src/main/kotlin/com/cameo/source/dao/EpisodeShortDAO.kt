package com.cameo.source.dao

import com.cameo.source.table.EpisodesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class EpisodeShortDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EpisodeShortDAO>(EpisodesTable)

    var title by EpisodesTable.title
    var episodeNumber by EpisodesTable.episodeNumber
    var airDate by EpisodesTable.airDate
    var image by EpisodesTable.posterPath
    var season by SeasonShortDAO referencedOn EpisodesTable.seasonId
}