package com.refit.app.data.me.model

data class OrderItemDto(
    val orderItemId: Long,
    val productId: Long,
    val orderCode: String,
    val productName: String,
    val thumbnailUrl: String,
    val createdAt: String, // ISO string (ZonedDateTime → 문자열)

    val unitPrice: Long,
    val originalUnitPrice: Long,
    val discountRate: Long,
    val lineAmount: Long,

    val status: Long,
    val quantity: Long,
    val canceledCount: Long,
    val quantityRemaining: Long,
    val brand: String,

    // 주문 레벨(아이템에도 함께 실려옴)
    val originalMerchandiseTotal: Long,
    val currentMerchandiseSubtotal: Long,
    val freeShippingApplied: Int,
    val deliveryFee: Long
)

data class OrderResponse(
    val orderId: String,
    val items: List<OrderItemDto>,

    val originalMerchandiseTotal: Long,
    val currentMerchandiseSubtotal: Long,
    val freeShippingApplied: Int,
    val deliveryFee: Long
)

data class OrdersResponse(
    val recentOrder: List<OrderResponse>
)
