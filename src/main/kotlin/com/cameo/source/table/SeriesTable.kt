package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object SeriesTable : IntIdTable() {
    val tmdbId = integer("tmdbId").uniqueIndex()
    val title = varchar("title", 2048)
    val originalTitle = varchar("original_title", 2048)
    val overview = text("overview")
    val firstAirDate = long("first_air_date")
    val lastAirDate = long("last_air_date")
    val homepage = varchar("homepage", 1024)
    val popularity = float("popularity").index()
    val status = varchar("status", 1024)
    val posterPath = varchar("poster_path", 1024)
    val backdropPath = varchar("backdrop_path", 1024)
}