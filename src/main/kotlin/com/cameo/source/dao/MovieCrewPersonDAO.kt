package com.cameo.source.dao

import com.cameo.source.table.MovieCrewPersonsTable
import com.cameo.source.table.PersonsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class MovieCrewPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieCrewPersonDAO>(MovieCrewPersonsTable)

    var department by MovieCrewPersonsTable.department
    var job by MovieCrewPersonsTable.job
    var person by PersonShortDAO referencedOn MovieCrewPersonsTable.personId
    var movie by MovieShortDAO referencedOn MovieCrewPersonsTable.movieId
}