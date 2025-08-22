package com.refit.app.data.cart.model

data class CartAddRequest(
    val productId: Long,
    val quantity: Int
)