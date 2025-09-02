package com.refit.app.data.analysis.modelAndView

// 업로드 화면에서 선택한 제품 타입(뷰티=화장품 / 헬스=영양제)
enum class ProductTypeUi { BEAUTY, HEALTH }

// UI 전용 상태 모델 (타입별 화면 분기)
sealed class UiResult {
    data object Loading : UiResult()
    data class Error(val msg: String) : UiResult()
    data object Empty : UiResult()

    // 화장품: 기존 상세 레이아웃
    data class Cosmetic(
        val memberName: String,
        val matchRate: Int,
        val risky: List<String>,
        val caution: List<String>,
        val safe: List<String>,
        val riskyText: String?,
        val cautionText: String?,
        val safeText: String?,
        val summary: String
    ) : UiResult()

    // 영양제: 전체 요약 + 주의 사항(2문단)
    data class Supplement(
        val memberName: String,
        val summary: String,
        val cautionText: String? // cautionText 우선, 없으면 riskyText 폴백
    ) : UiResult()
}