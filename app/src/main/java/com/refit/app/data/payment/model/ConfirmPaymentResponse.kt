package com.refit.app.data.payment.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPaymentResponse(
    val paymentId: Long,
    val paymentKey: String,
    val totalAmount: Long,
    val status: String,
    val receiptUrl: String?,
    val orderPk: Long? = null
)
