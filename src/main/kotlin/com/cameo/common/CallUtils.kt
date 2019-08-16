package com.cameo.common

import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal

fun ApplicationCall.getIntRequestParam(name: String) = request.queryParameters[name]?.toIntOrNull()

fun ApplicationCall.getStringRequestParam(name: String) = request.queryParameters[name].orEmpty()

fun ApplicationCall.getUserId() = authentication.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt()