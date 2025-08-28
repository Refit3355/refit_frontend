package com.refit.app.data.order.model

data class DraftOrderResponse(
    val orderCode: String,
    val orderId: Long,
    val orderSummary: String,
    val totalAmount: Long,
    val items: List<OrderItemSummary>,
    val shipping: ShippingInfo
)

data class OrderItemSummary(
    val productId: Long,
    val productName: String,
    val brandName: String,
    val thumbnailUrl: String,
    val originalPrice: Long, // 정가(스냅샷)
    val discountRate: Long,   // %
    val price: Long,         // 할인가(단가, 스냅샷)
    val quantity: Int
)

data class ShippingInfo(
    val receiverName: String,
    val phone: String,
    val roadAddress: String,
    val detailAddress: String,
    val zipcode: Long,
    val memo: String?
)
