package com.cameo.controller

import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getSeasons.GetSeasonsUseCase
import com.cameo.useCase.markSeasonNotWatched.MarkSeasonNotWatchedUseCase
import com.cameo.useCase.markSeasonWatched.MarkSeasonWatchedUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.put

class SeasonsController(private val seasonsUseCase: GetSeasonsUseCase,
                        private val markSeasonWatchedUseCase: MarkSeasonWatchedUseCase,
                        private val markSeasonNotWatchedUseCase: MarkSeasonNotWatchedUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedGetSeasons(call) }
            put("/") { proceedMarkSeasonWatched(call) }
            delete("/") { proceedMarkSeasonNotWatched(call) }
        }
    }

    private suspend fun proceedGetSeasons(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val seriesId = call.getIntRequestParam("seriesId") ?: throw IllegalArgumentException()
        call.respond(seasonsUseCase.execute(userId, seriesId))
    }

    private suspend fun proceedMarkSeasonWatched(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val seasonId = call.getIntRequestParam("seasonId") ?: throw IllegalArgumentException()
        call.respond(markSeasonWatchedUseCase.execute(seasonId, userId))
    }

    private suspend fun proceedMarkSeasonNotWatched(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val seasonId = call.getIntRequestParam("seasonId") ?: throw IllegalArgumentException()
        call.respond(markSeasonNotWatchedUseCase.execute(seasonId, userId))
    }
}