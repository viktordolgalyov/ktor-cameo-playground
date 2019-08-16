package com.cameo.source.dao

import com.cameo.source.table.EpisodesTable
import com.cameo.source.table.SeasonsTable
import com.cameo.source.table.UsersWatchedEpisodesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UsersWatchedEpisodesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UsersWatchedEpisodesDAO>(EpisodesTable)

    val seriesId by SeasonsTable.seriesId

    val seasonId by SeasonsTable.id
    val seasonTitle by SeasonsTable.title
    val seasonNumber by SeasonsTable.seasonNumber
    val seasonAirDate by SeasonsTable.airDate

    val episodeId by EpisodesTable.id
    val episodeTitle by EpisodesTable.title
    val episodeOverview by EpisodesTable.overview
    val episodeNumber by EpisodesTable.episodeNumber
    val episodePoster by EpisodesTable.posterPath
    val episodeAirDate by EpisodesTable.airDate

    val watchedTimestamp by UsersWatchedEpisodesTable.watchedTimestamp
}