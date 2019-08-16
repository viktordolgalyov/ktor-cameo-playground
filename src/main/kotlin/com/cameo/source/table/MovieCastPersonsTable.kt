package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object MovieCastPersonsTable : IntIdTable() {
    val movieId = reference("movie_id", MoviesTable).primaryKey()
    val personId = reference("person_id", PersonsTable).primaryKey()
    val character = text("character").nullable()
    val signature = text("signature").uniqueIndex()//formatted as movieId_personId_character
}