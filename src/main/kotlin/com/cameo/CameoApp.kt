package com.cameo

import com.cameo.common.data.ExposedDatabase
import com.cameo.common.data.installJwt
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.Routing

fun Application.main() {
    install(CallLogging)
    install(ContentNegotiation) { gson { } }
    install(Authentication) { installJwt(environment) }
    install(DefaultHeaders)
    install(StatusPages)
    ExposedDatabase.install()
    install(Routing) { proceed() }
}