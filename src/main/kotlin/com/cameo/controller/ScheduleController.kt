package com.cameo.controller

import com.cameo.common.data.toDateTime
import com.cameo.common.getStringRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.followItem.FollowItemRequest
import com.cameo.useCase.followItem.FollowItemUseCase
import com.cameo.useCase.getSchedule.GetUserScheduleUseCase
import com.cameo.useCase.unfollowItem.UnfollowItemRequest
import com.cameo.useCase.unfollowItem.UnfollowItemUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post

class ScheduleController(private val userScheduleUseCase: GetUserScheduleUseCase,
                         private val followItemUseCase: FollowItemUseCase,
                         private val unfollowItemUseCase: UnfollowItemUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedGetUserSchedule(call) }
            post("/") { proceedAddItemToSchedule(call) }
            delete("/") { proceedDeleteItemFromSchedule(call) }
        }
    }

    private suspend fun proceedGetUserSchedule(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val from = call.getStringRequestParam("from").toDateTime()
        val to = call.getStringRequestParam("to").toDateTime()
        call.respond(userScheduleUseCase.execute(userId, from.millis / 1000, to.millis / 1000))
    }

    private suspend fun proceedAddItemToSchedule(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val request = call.receive(FollowItemRequest::class)
        call.respond(followItemUseCase.execute(userId, request))
    }

    private suspend fun proceedDeleteItemFromSchedule(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val request = call.receive(UnfollowItemRequest::class)
        call.respond(unfollowItemUseCase.execute(userId, request))
    }
}