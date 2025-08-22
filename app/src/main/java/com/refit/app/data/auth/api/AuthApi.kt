package com.refit.app.data.auth.api

import com.refit.app.data.auth.model.LoginRequest
import com.refit.app.data.auth.model.LoginResponse
import com.refit.app.data.auth.model.SignupAllRequest
import com.refit.app.data.auth.model.SignupResponse
import com.refit.app.data.auth.model.UtilResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/join")
    suspend fun join(@Body body: SignupAllRequest): UtilResponse<SignupResponse>

    @POST("/auth/login")
    suspend fun login(
        @Body req: LoginRequest
    ): Response<UtilResponse<LoginResponse>>

    @GET("/auth/check/email")
    suspend fun checkEmail(@Query("email") email: String): UtilResponse<Boolean>

    @GET("/auth/check/nickname")
    suspend fun checkNickname(@Query("nickname") nickname: String): UtilResponse<Boolean>
}