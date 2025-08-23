package com.refit.app.data.auth.model

data class UtilResponse<T>(val status: String, val message: String, val data: T?)

data class SignupResponse(val id: Long)

data class SignupAllRequest(
    val signup: SignupRequest,
    val concerns: ConcernRequest
)

data class SignupRequest (
    val email: String,
    val nickName: String,
    val memberName: String,
    val password: String,
    val zipcode: String,
    val roadAddress: String,
    val detailAddress: String,
    val birthday: String, // 'yyyy-MM-dd' 형식
    val phoneNumber: String
)

data class ConcernRequest(
    val health: HealthInfoDto,
    val hair: HairInfoDto,
    val skin: SkinInfoDto
)

data class HealthInfoDto(
    val eyeHealth: Int, val fatigue: Int, val sleepStress: Int,
    val immuneCare: Int, val muscleHealth: Int, val gutHealth: Int,
    val bloodCirculation: Int
)

data class HairInfoDto(
    val hairLoss: Int, val damagedHair: Int, val scalpTrouble: Int, val dandruff: Int
)

data class SkinInfoDto(
    val atopic: Int, val acne: Int, val whitening: Int, val sebum: Int,
    val innerDryness: Int, val wrinkles: Int, val enlargedPores: Int,
    val redness: Int, val keratin: Int,
    val skinType: Int? = null
)