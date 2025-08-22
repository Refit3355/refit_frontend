package com.refit.app.data.cart.model

data class CartDeleteBulkRequest (
    val deletedItems: List<Long>
)