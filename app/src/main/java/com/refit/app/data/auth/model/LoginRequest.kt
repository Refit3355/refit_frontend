package com.refit.app.data.auth.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val memberId: Long,
    val nickname: String,
    val health: HealthInfoDto?,
    val refreshToken: String?
)
