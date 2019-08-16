package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object SeriesCrewPersonsTable : IntIdTable() {
    val seriesId = reference("series_id", SeriesTable).primaryKey()
    val personId = reference("person_id", PersonsTable).primaryKey()
    val department = varchar("department", 256).nullable()
    val job = varchar("job", 256).nullable()
    val signature = text("signature").uniqueIndex()// seriesId_personId_job
}