package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object EpisodesTable : IntIdTable() {
    val seasonId = reference("season_id", SeasonsTable).index()
    val title = varchar("title", 1024)
    val overview = text("overview")
    val airDate = long("air_date").index()
    val episodeNumber = integer("episode_number")
    val posterPath = varchar("poster_path", 1024)
    val signature = text("signature").uniqueIndex()//formatted as seasonId_episodeNumber
}