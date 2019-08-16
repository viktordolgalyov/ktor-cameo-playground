package com.cameo.controller

import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getSeriesDetails.GetSeriesDetailsUseCase
import com.cameo.useCase.getSeriesTrailers.GetSeriesTrailersUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

class SeriesController(private val seriesDetailsUseCase: GetSeriesDetailsUseCase,
                       private val seriesTrailersUseCase: GetSeriesTrailersUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedSeriesDetails(call) }
            get("/trailers") { proceedSeriesTrailers(call) }
        }
    }

    private suspend fun proceedSeriesDetails(call: ApplicationCall) {
        val seriesId = call.getIntRequestParam("id") ?: throw IllegalArgumentException()
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val details = seriesDetailsUseCase.execute(userId, seriesId) ?: throw IllegalArgumentException()
        call.respond(details)
    }

    private suspend fun proceedSeriesTrailers(call: ApplicationCall) {
        val seriesId = call.getIntRequestParam("id") ?: throw IllegalArgumentException()
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        call.respond(seriesTrailersUseCase.execute(seriesId, userId))
    }
}