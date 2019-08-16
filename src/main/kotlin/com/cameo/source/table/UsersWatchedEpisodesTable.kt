package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object UsersWatchedEpisodesTable : IntIdTable() {
    val signature = text("signature").uniqueIndex()
    val userId = reference("user_id", UsersTable).primaryKey()
    val seasonId = reference("season_id", SeasonsTable).primaryKey()
    val episodeId = reference("episode_id", EpisodesTable).primaryKey()
    val watchedTimestamp = long("watched_ts").nullable()
}