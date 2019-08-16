package com.cameo.source.dao

import com.cameo.source.table.MovieCastPersonsTable
import com.cameo.source.table.PersonsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class MovieCastPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieCastPersonDAO>(MovieCastPersonsTable)

    var movie by MovieShortDAO referencedOn MovieCastPersonsTable.movieId
    var person by PersonShortDAO referencedOn MovieCastPersonsTable.personId
    var character by MovieCastPersonsTable.character
}