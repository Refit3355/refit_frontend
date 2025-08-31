package com.refit.app.data.me.model

data class PartialCancelResponseDto(
    val paymentId: Long,
    val canceledAmount: Long,
    val balanceAmount: Long,
    val status: String,       // "PARTIAL_CANCELED" | "CANCELED"
    val canceledAt: String
)