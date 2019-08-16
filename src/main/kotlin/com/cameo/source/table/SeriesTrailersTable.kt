package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object SeriesTrailersTable : IntIdTable() {
    val seriesId = reference("series_id", SeriesTable)
    val trailerPath = text("trailer_path")
    val signature = text("signature").uniqueIndex()//formatted as seriesId_trailerPath
}