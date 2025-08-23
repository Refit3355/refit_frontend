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
    val nickname: String? = null,
    val name: String? = null,
    val password: String? = null,
    val zipcode: String? = null,
    val roadAddress: String? = null,
    val detailAddress: String? = null,
    val birthday: String? = null,
    val phone: String? = null
)