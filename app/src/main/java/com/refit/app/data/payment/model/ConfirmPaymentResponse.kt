package com.refit.app.data.payment.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPaymentResponse(
    val paymentId: Long,
    val paymentKey: String,
    val totalAmount: Long,
    val status: String,
    val receiptUrl: String?,
    val orderPk: Long? = null,
    val orderCode: String,
    val orderName: String,
    val method: String,
    val firstItemThumb: String,
    val itemCount: Int,
    val items: List<ConfirmPaymentItem>
)

@Serializable
data class ConfirmPaymentItem(
    val productId: Long,
    val brandName: String,
    val productName: String,
    val price: Long,
    val originalPrice: Long,
    val quantity: Int,
    val thumnailUrl: String
)
