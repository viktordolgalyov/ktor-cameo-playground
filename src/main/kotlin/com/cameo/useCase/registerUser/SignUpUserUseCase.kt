package com.cameo.useCase.registerUser

import com.cameo.common.data.getPasswordHash
import com.cameo.common.requestThread
import com.cameo.source.dao.UserDAO
import com.cameo.source.table.UsersTable
import io.ktor.auth.UserPasswordCredential

class SignUpUserUseCase {

    suspend fun execute(credential: UserPasswordCredential) = requestThread {
        when {
            isUserExists(credential.name) -> throw IllegalArgumentException()
            else -> createUser(credential)
        }
    }

    private fun isUserExists(username: String): Boolean {
        return UserDAO.find { UsersTable.username eq username }
                .empty()
                .not()
    }

    private fun createUser(credential: UserPasswordCredential): Int {
        return UserDAO.new {
            this.username = credential.name
            this.passwordHash = getPasswordHash(credential.password)
        }.id.value
    }
}