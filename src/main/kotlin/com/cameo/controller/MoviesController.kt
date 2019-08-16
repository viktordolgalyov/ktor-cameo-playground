package com.cameo.controller

import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getMovieDetails.GetMovieDetailsUseCase
import com.cameo.useCase.getMovieTrailers.GetMovieTrailersUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

class MoviesController(private val movieDetailsUseCase: GetMovieDetailsUseCase,
                       private val movieTrailersUseCase: GetMovieTrailersUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedMovieDetails(call) }
            get("/trailers") { proceedMovieTrailers(call) }
        }
    }

    private suspend fun proceedMovieDetails(call: ApplicationCall) {
        val movieId = call.getIntRequestParam("id") ?: throw IllegalArgumentException()
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val details = movieDetailsUseCase.execute(userId, movieId) ?: throw IllegalArgumentException()
        call.respond(details)
    }

    private suspend fun proceedMovieTrailers(call: ApplicationCall) {
        val movieId = call.getIntRequestParam("id") ?: throw IllegalArgumentException()
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val trailers = movieTrailersUseCase.execute(movieId, userId)
        call.respond(trailers)
    }
}