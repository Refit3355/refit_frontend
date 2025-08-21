package com.refit.app.ui.fake

data class RecentOrderDummy(
    val orderNumber: String,
    val date: String,
    val items: List<RecentOrderItemDummy>
)

data class RecentOrderItemDummy(
    val productName: String,
    val imageUrl: String,
    val price: Int,          // 실제 결제가
    val originalPrice: Int,  // 정가
    val quantity: Int,       // 수량
    val status: String       // 주문 상태 (배송중, 배송완료, 취소완료 등)
)

// 샘플 데이터
val sampleRecentOrder = RecentOrderDummy(
    orderNumber = "1234567890",
    date = "2025-08-20",
    items = listOf(
        RecentOrderItemDummy(
            productName = "프리미엄 에센스",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyGTj_WA8cQvrQNel7voddzEN5LwDVvomPeA&s",
            price = 29000,
            originalPrice = 35000,
            quantity = 2,
            status = "배송중"
        ),
        RecentOrderItemDummy(
            productName = "비타민 세트",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyGTj_WA8cQvrQNel7voddzEN5LwDVvomPeA&s",
            price = 15000,
            originalPrice = 18000,
            quantity = 1,
            status = "배송완료"
        ),
        RecentOrderItemDummy(
            productName = "콜라겐 파우더",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyGTj_WA8cQvrQNel7voddzEN5LwDVvomPeA&s",
            price = 22000,
            originalPrice = 25000,
            quantity = 3,
            status = "취소완료"
        )
    )
)
