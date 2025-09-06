package com.refit.app.data.chatbot.model

data class Product(
    val id: Long,
    val thumbnailUrl: String,
    val brand: String,
    val name: String,
    val discountRate: Int,
    val originalPrice: Long,
    val discountedPrice: Long,
    val sales: Int
)