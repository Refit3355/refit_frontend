package com.refit.app.data.auth.model

data class KakaoVerifyRequest(
    val accessToken: String
)

data class KakaoSignupRequest(
    val accessToken: String,
    val signupAll: SignupAllRequest
)

data class KakaoVerifyResponse(
    val needSignup: Boolean,
    val kakaoId: String?,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,

    val memberId: Long?,
    val userEmail: String?,
    val userNickname: String?,
    val name: String?,
    val refreshToken: String?
)
