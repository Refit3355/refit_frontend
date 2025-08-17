package com.refit.app.data.product.model

data class Product(
    val id: Long,
    val image: String,
    val brand: String,
    val name: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int
)
