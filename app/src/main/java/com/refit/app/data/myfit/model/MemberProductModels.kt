package com.refit.app.data.myfit.model

data class MemberProductListResponse(
    val items: List<MemberProductItem> = emptyList(),
    val total: Int = 0
)

data class MemberProductItem(
    val memberProductId: Long,
    val bhType: Int,
    val thumbnailUrl: String? = null,
    val brandName: String,
    val productName: String,
    val categoryId: Long,
    val productId: Long?,          // 판매중인 상품일 때만 not null
    val status: String,            // using | completed
    val recommendedPeriod: Int?,   // 권장 사용일 (예: 360)
    val startDate: String?,
    val expiryDate: String?,
    val daysRemaining: Int?,       // 남은 일수 (음수면 경과)
    val displayRemaining: String?, // "D-30", "D+3"
    val usagePeriodText: String?,  // 완료 탭 전용 "yyyy-MM-dd ~ yyyy-MM-dd (총 x일)"
    val effects: List<Long> = emptyList()
)