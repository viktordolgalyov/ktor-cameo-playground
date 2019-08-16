package com.cameo.source.dao

import com.cameo.source.table.PersonsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class PersonShortDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PersonShortDAO>(PersonsTable)

    var name by PersonsTable.name
    var photoPath by PersonsTable.photoPath
}