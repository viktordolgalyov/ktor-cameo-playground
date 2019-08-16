package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object SeriesCastPersonsTable : IntIdTable() {
    val seriesId = reference("series_id", SeriesTable).primaryKey()
    val personId = reference("person_id", PersonsTable).primaryKey()
    val character = text("character").nullable()
    val signature = text("signature").uniqueIndex()//seriesId_personId_character
}