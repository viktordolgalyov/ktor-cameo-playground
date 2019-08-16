package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object MovieCrewPersonsTable : IntIdTable() {
    val movieId = reference("movie_id", MoviesTable).primaryKey()
    val personId = reference("person_id", PersonsTable).primaryKey()
    val department = varchar("department", 256).nullable()
    val job = varchar("job", 256).nullable()
    val signature = text("signature") // formatted as movieId_personId_job
}