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
    val items: List<ConfirmPaymentItem>,

//   가상계좌용
    val vaAccountNo: String? = null,
    val vaBankCode: String? = null,
    val vaDueDate: String? = null,         // ISO8601(예: 2025-09-13T15:53:34+09:00)
    val vaDepositorName: String? = null
)

@Serializable
data class ConfirmPaymentItem(
    val productId: Long,
    val brandName: String,
    val productName: String,
    val price: Long,
    val originalPrice: Long,
    val quantity: Int,
    val thumbnailUrl: String
)
