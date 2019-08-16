package com.cameo.controller

import com.cameo.common.data.PaginationInfo
import com.cameo.common.getIntRequestParam
import com.cameo.common.getUserId
import com.cameo.useCase.getUserList.GetUserListUseCase
import com.cameo.useCase.loginUser.LoginUserUseCase
import com.cameo.useCase.registerUser.SignUpUserUseCase
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

class UserController(private val loginUserUseCase: LoginUserUseCase,
                     private val signUpUserUseCase: SignUpUserUseCase,
                     private val userListUseCase: GetUserListUseCase) {

    fun proceed(route: Route) = with(route) {
        post("/login") { proceedLogin(call) }
        post("/register") { proceedSignUp(call) }
        authenticate("jwt") {
            get("/list") { proceedUserList(call) }
        }
    }

    private suspend fun proceedLogin(call: ApplicationCall) {
        val credentials = call.receive(UserPasswordCredential::class)
        call.respond(loginUserUseCase.execute(credentials))
    }

    private suspend fun proceedSignUp(call: ApplicationCall) {
        val credentials = call.receive(UserPasswordCredential::class)
        signUpUserUseCase.execute(credentials)
        call.respond(loginUserUseCase.execute(credentials))
    }

    private suspend fun proceedUserList(call: ApplicationCall) {
        val userId = call.getUserId() ?: throw IllegalArgumentException()
        val page = call.getIntRequestParam("page") ?: 0
        call.respond(userListUseCase.execute(userId, PaginationInfo(page)))
    }
}