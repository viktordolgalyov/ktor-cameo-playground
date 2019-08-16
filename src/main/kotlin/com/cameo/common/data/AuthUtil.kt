package com.cameo.common.data

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cameo.source.dao.UserDAO
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.util.getDigestFunction
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.experimental.and

private const val PASSWORD_SALT = "salt"
private const val JWT_SECRET = "secret"

fun getPasswordHash(password: String): String {
    return getDigestFunction("SHA-512", PASSWORD_SALT).invoke(password).map {
        Integer.toString((it and 0xff.toByte()) + 0x100, 16).substring(1)
    }.joinToString(separator = "", truncated = "")
}

fun makeToken(user: UserDAO): String = JWT.create()
        .withAudience("jwt-audience")
        .withSubject("Authentication")
        .withClaim("id", user.id.value)
        .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)))
        .sign(Algorithm.HMAC512(JWT_SECRET))

private fun getJWTVerifier(audience: String) = JWT
        .require(Algorithm.HMAC512(JWT_SECRET))
        .withAudience(audience)
        .withSubject("Authentication")
        .build()

fun Authentication.Configuration.installJwt(env: ApplicationEnvironment) {
    val audience = env.config.property("jwt.audience").getString()
    val realm = env.config.property("jwt.realm").getString()
    jwt("jwt") {
        verifier(getJWTVerifier(audience))
        this.realm = realm
        validate {
            when {
                it.payload.audience.contains(audience) -> JWTPrincipal(it.payload)
                else -> null
            }
        }
    }
}