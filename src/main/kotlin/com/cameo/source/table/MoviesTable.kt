package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object MoviesTable : IntIdTable() {
    val tmdbId = integer("tmdb_id").uniqueIndex()
    val imdbId = varchar("imdb_id", 128)
    val title = varchar("title", 1024)
    val originalTitle = varchar("original_title", 1024)
    val overview = text("overview")
    val popularity = float("popularity").index()
    val releaseDate = long("release_date").index()
    val collectionId = integer("collection_id")
    val budget = long("budget")
    val homepage = varchar("homepage", 1024)
    val originalLanguage = varchar("original_language", 128)
    val revenue = long("revenue")
    val runtime = integer("runtime")
    val tagline = text("tagline")
    val status = varchar("status", 256)
    val posterPath = text("poster_path")
    val backdropPath = text("backdrop_path")
}