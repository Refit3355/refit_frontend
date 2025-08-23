package com.refit.app.data.me.model

data class CombinationsResponse(
    val combinations: List<CombinationDto>
)

data class CombinationDto(
    val combinationId: Long,
    val memberId: Long?,
    val nickname: String?,
    val profileUrl: String?,
    val combinationName: String,
    val likes: Long?,
    val originalTotalPrice: Long,
    val discountedTotalPrice: Long,
    val productImages: List<String>
)
