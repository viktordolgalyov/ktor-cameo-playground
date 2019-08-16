package com.cameo.source.dao

import com.cameo.source.table.SeriesTrailersTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesTrailerDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesTrailerDAO>(SeriesTrailersTable)

    var trailerPath by SeriesTrailersTable.trailerPath
}