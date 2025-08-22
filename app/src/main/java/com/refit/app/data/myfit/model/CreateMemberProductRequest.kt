package com.refit.app.data.myfit.model

data class CreateMemberProductRequest (
    val type: String,                 // "beauty" | "health"
    val productName: String,
    val brandName: String,
    val recommendedPeriodDays: Int,
    val startDate: String,            // "YYYY-MM-DD"
    val categoryId: Long,
    val effect: List<Int>? = null
)