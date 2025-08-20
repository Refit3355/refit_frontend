package com.refit.app.data.auth.api

import com.refit.app.data.auth.model.SignupAllRequest
import com.refit.app.data.auth.model.SignupResponse
import com.refit.app.data.auth.model.UtilResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/join")
    suspend fun join(@Body body: SignupAllRequest): UtilResponse<SignupResponse>

    @POST("auth/login")
    suspend fun login(
        @Body req: LoginRequest
    ): Response<UtilResponse<LoginResponse>>
}