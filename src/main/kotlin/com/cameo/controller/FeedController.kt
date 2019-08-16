package com.cameo.controller

import com.cameo.common.data.PaginationInfo
import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getFeed.GetFeedUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

class FeedController(private val feedUseCase: GetFeedUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedGetFeed(call) }
        }
    }

    private suspend fun proceedGetFeed(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val page = call.getIntRequestParam("page") ?: 0
        val limit = call.getIntRequestParam("limit") ?: 20
        call.respond(feedUseCase.execute(userId, PaginationInfo(page, limit)))
    }
}