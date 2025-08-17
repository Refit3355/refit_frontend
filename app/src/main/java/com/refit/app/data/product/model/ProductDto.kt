package com.refit.app.data.product.model

data class ProductDto(
    val id: Long,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int,
    val sales: Int
)