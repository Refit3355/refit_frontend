package com.refit.app.data.me.model

data class LikeRequest(
    val likedItems: List<Long>
)

data class LikeProductDto(
    val id: Long,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int,
    val sales: Int
)

data class LikeResponse(
    val items: List<LikeProductDto>,
    val totalCount: Int,
    val hasMore: Boolean,
    val nextCursor: Long?
)