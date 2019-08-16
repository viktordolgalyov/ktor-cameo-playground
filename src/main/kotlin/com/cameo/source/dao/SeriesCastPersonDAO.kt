package com.cameo.source.dao

import com.cameo.source.table.SeriesCastPersonsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesCastPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesCastPersonDAO>(SeriesCastPersonsTable)

    var series by SeriesShortDAO referencedOn SeriesCastPersonsTable.seriesId
    var person by PersonShortDAO referencedOn SeriesCastPersonsTable.personId
    var character by SeriesCastPersonsTable.character
}