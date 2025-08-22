package com.refit.app.data.auth.model

data class BasicInfoResponse (
    val memberId: Long,
    val email: String,
    val nickname: String,
    val memberName: String,
    val zipcode: String?,
    val roadAddress: String?,
    val detailAddress: String?,
    val birthday: String?,      // "yyyy-MM-dd"
    val phoneNumber: String?
)

data class UpdateBasicRequest(
    val email: String?,
    val name: String?,
    val password: String?,
    val zipcode: String?,
    val roadAddress: String?,
    val detailAddress: String?,
    val birthday: String?,
    val phoneNumber: String?
)