package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object SeasonsTable : IntIdTable() {
    val seriesId = reference("series_id", SeriesTable)
    val title = varchar("title", 1024)
    val overview = text("overview")
    val airDate = long("air_date").index()
    val seasonNumber = integer("season_number")
    val posterPath = varchar("poster_path", 1024)
    val signature = text("signature").uniqueIndex()
}