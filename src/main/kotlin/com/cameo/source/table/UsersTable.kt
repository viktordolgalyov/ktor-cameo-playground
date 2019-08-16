package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object UsersTable : IntIdTable() {
    val username = varchar("username", 256)
    val passwordHash = varchar("passwordHash", 1024)
}