package com.cameo.controller

import com.cameo.common.data.PaginationInfo
import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getOnboarding.GetOnboardingUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

class OnboardingController(private val onboardingItemsUseCase: GetOnboardingUseCase) {

    fun proceed(route: Route) = with(route) {
        authenticate("jwt") {
            get("/") { proceedGetOnboarding(call) }
        }
    }

    private suspend fun proceedGetOnboarding(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val page = call.getIntRequestParam("page") ?: 0
        call.respond(onboardingItemsUseCase.execute(userId, PaginationInfo(page)))
    }
}