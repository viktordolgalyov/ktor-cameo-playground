package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object MovieTrailersTable : IntIdTable() {
    val movieId = reference("movie_id", MoviesTable)
    val trailerPath = text("trailer_path")
    val signature = text("signature").uniqueIndex()
}