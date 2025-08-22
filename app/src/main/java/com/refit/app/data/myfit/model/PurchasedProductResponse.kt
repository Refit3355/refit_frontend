package com.refit.app.data.myfit.model

data class PurchasedProductDto(
    val orderItemId: Long,
    val productId: Long,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val itemCount: Int,
    val price: Int,
    val discountRate: Int,
    val discountedPrice: Int,
    val purchaseDate: String
)

data class PurchasedProductResponse(
    val data: List<PurchasedProductDto>
)