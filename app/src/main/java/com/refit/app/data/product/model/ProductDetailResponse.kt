package com.refit.app.data.product.model

// DTO & Response
data class ProductDetailResponse(
    val product: ProductDetailDto,
    val images: List<ProductImageDto>
)

data class ProductDetailDto(
    val id: Int,
    val brandName: String,
    val thumbnailUrl: String,
    val productName: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int,
    val recommendedPeriod: String
)

data class ProductImageDto(
    val id: Int,
    val url: String,
    val order: Int
)

// 도메인 모델
data class ProductImage(val id: Int, val url: String, val order: Int)

data class ProductDetail(
    val id: Int,
    val brand: String,
    val name: String,
    val thumbnailUrl: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int,
    val recommendedPeriod: String,
    val images: List<ProductImage>
)
