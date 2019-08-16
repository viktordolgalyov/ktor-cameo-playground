package com.cameo.source.dao

import com.cameo.source.table.UsersTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UsersTable)

    var username by UsersTable.username
    var passwordHash by UsersTable.passwordHash
}