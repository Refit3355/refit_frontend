package com.refit.app.data.local.combination.model

data class CombinationResponse(
    val combinationId: Long,
    val memberId: Long,
    val nickname: String,
    val profileUrl: String,
    val combinationName: String,
    val discountPrice: Int,
    val originalPrice: Int,
    val products: List<CombinationItemImageDto>,
    val likes: Int,
)

data class CombinationItemImageDto(
    val productId: Long,
    val imageUrl: String
)
