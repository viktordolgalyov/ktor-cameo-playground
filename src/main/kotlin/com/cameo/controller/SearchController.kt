package com.cameo.controller

import com.cameo.common.data.PaginationInfo
import com.cameo.common.getIntRequestParam
import com.cameo.common.getStringRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getSearchItems.GetSearchItemsUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

class SearchController(private val searchItemsUseCase: GetSearchItemsUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedGetSearchResults(call) }
        }
    }

    private suspend fun proceedGetSearchResults(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val query = call.getStringRequestParam("q")
        val page = call.getIntRequestParam("page") ?: 0
        val limit = call.getIntRequestParam("count") ?: 15
        call.respond(searchItemsUseCase.execute(userId, query, PaginationInfo(page, limit)))
    }
}