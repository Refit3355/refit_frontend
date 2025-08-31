package com.refit.app.data.me.model

data class PartialCancelRequestDto(
    val cancelReason: String,
    val cancelAmount: Int,
    val taxFreeAmount: Long? = null,
    val idempotencyKey: String? = null
)