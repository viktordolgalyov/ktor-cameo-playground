package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object PersonsTable : IntIdTable() {
    val tmdbId = integer("tmdb_id").uniqueIndex()
    val imdbId = varchar("imdb_id", 128)
    val name = text("name")
    val biography = text("biography")
    val birthday = varchar("birthday", 64)
    val deathday = varchar("deathday", 64)
    val homepage = text("homepage")
    val birthplace = text("birthplace")
    val popularity = float("popularity")
    val photoPath = varchar("photo_path", 1024)
}