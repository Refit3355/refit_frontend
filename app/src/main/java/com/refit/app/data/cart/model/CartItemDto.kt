package com.refit.app.data.cart.model

data class CartItemDto(
    val cartId: Long,
    val cartCnt: Int,
    val id: Long,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val discountRate: Int?,
    val price: Int,
    val discountedPrice: Int
)