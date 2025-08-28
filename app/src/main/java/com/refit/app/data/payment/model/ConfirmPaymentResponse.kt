package com.refit.app.data.payment.model

data class ConfirmPaymentResponse(
    val paymentId: Long,
    val paymentKey: String,
    val totalAmount: Long,
    val status: String,
    val receiptUrl: String?
)
