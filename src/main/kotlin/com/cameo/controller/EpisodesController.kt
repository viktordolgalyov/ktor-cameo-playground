package com.cameo.controller

import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.markEpisodeNotWatched.MarkEpisodeNotWatchedUseCase
import com.cameo.useCase.markEpisodeWatched.MarkEpisodeWatchedUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get

class EpisodesController(private val markEpisodeWatchedUseCase: MarkEpisodeWatchedUseCase,
                         private val markEpisodeNotWatchedUseCase: MarkEpisodeNotWatchedUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedSetEpisodeWatched(call) }
            delete("/") { proceedDeleteEpisodeWatched(call) }
        }
    }

    private suspend fun proceedSetEpisodeWatched(call: ApplicationCall) {
        val episodeId = call.getIntRequestParam("episodeId") ?: throw IllegalArgumentException()
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        call.respond(markEpisodeWatchedUseCase.execute(userId, episodeId))
    }

    private suspend fun proceedDeleteEpisodeWatched(call: ApplicationCall) {
        val episodeId = call.getIntRequestParam("episodeId") ?: throw IllegalArgumentException()
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        call.respond(markEpisodeNotWatchedUseCase.execute(userId, episodeId))
    }
}