package com.refit.app.data.payment.model

data class ConfirmPaymentRequest(
    val paymentKey: String,
    val orderId: String, // ORDER_CODE
    val amount: Long
)