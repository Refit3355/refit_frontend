package com.refit.app.util.order

object OrderStatusMapper {
    fun getStatusText(status: Int): String {
        return when (status) {
            0 -> "결제 전"
            1 -> "결제 완료"
            2 -> "부분 결제 취소"
            3 -> "결제 취소"
            4 -> "결제 시간 만료"
            5 -> "배송 중"
            6 -> "배송 완료"
            7 -> "교환 신청"
            8 -> "교환 완료"
            9 -> "반품 신청"
            10 -> "반품 완료"
            11 -> "구매 확정"
            else -> "알수없음"
        }
    }
}
