package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object SeriesGenresTable : IntIdTable() {
    val seriesId = reference("series_id", SeriesTable).index()
    val genreId = integer("genre_id").nullable()
}