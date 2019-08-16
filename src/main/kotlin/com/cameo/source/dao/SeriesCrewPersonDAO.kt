package com.cameo.source.dao

import com.cameo.source.table.PersonsTable
import com.cameo.source.table.SeriesCrewPersonsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesCrewPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesCrewPersonDAO>(SeriesCrewPersonsTable)

    var department by SeriesCrewPersonsTable.department
    var job by SeriesCrewPersonsTable.job
    var person by PersonShortDAO referencedOn SeriesCrewPersonsTable.personId
    var series by SeriesShortDAO referencedOn SeriesCrewPersonsTable.seriesId
}