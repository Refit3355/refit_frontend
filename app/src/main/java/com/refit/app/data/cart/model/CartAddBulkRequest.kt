package com.refit.app.data.cart.model

data class CartAddBulkRequest (
    val items: List<CartAddRequest>
)