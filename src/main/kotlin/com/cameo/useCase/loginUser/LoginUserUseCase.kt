package com.cameo.useCase.loginUser

import com.cameo.common.requestThread
import com.cameo.common.data.getPasswordHash
import com.cameo.common.data.makeToken
import com.cameo.source.dao.UserDAO
import com.cameo.source.table.UsersTable
import io.ktor.auth.UserPasswordCredential

class LoginUserUseCase {

    suspend fun execute(credential: UserPasswordCredential): AuthResponse = requestThread {
        val existingUser = getUser(credential.name)
        existingUser?.let {
            when (getPasswordHash(credential.password)) {
                it.passwordHash -> AuthResponse(makeToken(it))
                else -> throw IllegalArgumentException()
            }
        } ?: throw IllegalArgumentException()
    }

    private fun getUser(username: String): UserDAO? {
        return UserDAO
                .find { UsersTable.username eq username }
                .firstOrNull()
    }
}