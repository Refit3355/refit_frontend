package com.refit.app.data.myfit.model

data class UpdateMemberProductRequest(
    val productName: String? = null,
    val brandName: String? = null,
    val categoryId: Long? = null,
    val effectIds: List<Long>? = null,
    val recommendedPeriod: Int? = null,
    val startDate: String? = null // YYYY-MM-DD
)