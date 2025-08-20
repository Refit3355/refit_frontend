package com.refit.app.data.product.model

data class RecommendationResponse(
    val items: List<RecommendationProduct>
)

data class RecommendationProduct(
    val productId: Int,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val discountRate: Double?,
    val price: Int,
    val discountedPrice: Int
)
