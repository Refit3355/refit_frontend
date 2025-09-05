package com.refit.app.data.chatbot.model

import com.refit.app.data.product.model.Product

data class ProductListResponseDto(
    val items: List<ProductItemDto>,
    val totalCount: Int
)

data class ProductItemDto(
    val productId: Long,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val discountRate: Int,
    val originalPrice: Long,
    val discountedPrice: Long,
    val sales: Int
)

data class UiProduct(
    val id: Long,
    val image: String,
    val brand: String,
    val name: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int
)

fun ProductItemDto.toUiProduct(): UiProduct = UiProduct(
    id = productId,
    image = thumbnailUrl,
    brand = brandName,
    name = productName,
    discountRate = discountRate,
    price = originalPrice.toInt(),
    discountedPrice = discountedPrice.toInt()
)