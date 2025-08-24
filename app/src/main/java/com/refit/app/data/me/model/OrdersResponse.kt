package com.refit.app.data.me.model

data class OrderItemDto(
    val orderItemId: Long,
    val orderNumber: String,
    val productName: String,
    val thumbnailUrl: String,
    val createdAt: String,
    val price: Int,
    val originalPrice: Int,
    val status: Int,
    val quantity: Int,
    val brand: String
)

data class OrderResponse(
    val orderId: String,
    val items: List<OrderItemDto>
)

data class OrdersResponse(
    val recentOrder: List<OrderResponse>
)
