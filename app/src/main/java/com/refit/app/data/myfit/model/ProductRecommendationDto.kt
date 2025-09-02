package com.refit.app.data.myfit.model

data class ProductRecommendationDto(
    val productId: Long,
    val categoryId: Int?,
    val productName: String,
    val brandName: String,
    val price: Int,
    val thumbnailUrl: String,
    val discountRate: Int,
    val discountedPrice: Int,
    val stock: Long?,
    val score: Double?,
    val baseSimilarity: Double?,
    val rankOrder: Int?,
    val effectIds: List<Long>?
)