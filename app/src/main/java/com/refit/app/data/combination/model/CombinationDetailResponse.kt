package com.refit.app.data.combination.model

data class CombinationDetailResponse(
    val combinationId: Long,
    val combinationName: String,
    val combinationDescription: String,
    val memberId: Long,
    val nickname: String,
    val profileUrl: String,
    val originalTotalPrice: Long,
    val discountedTotalPrice: Long,
    val products: List<CombinationProductDto>
)

data class CombinationProductDto(
    val productId: Long,
    val brandName: String,
    val productName: String,
    val price: Long,
    val discountRate: Int,
    val thumbnailUrl: String,
    val discountedPrice: Long
)
