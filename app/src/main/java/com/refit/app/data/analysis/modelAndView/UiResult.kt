package com.refit.app.data.analysis.modelAndView

enum class ProductTypeUi { BEAUTY, HEALTH }

sealed class UiResult {
    data object Loading : UiResult()
    data class Error(val msg: String) : UiResult()
    data object Empty : UiResult()

    data class Blocked(
        val title: String,
        val message: String
    ) : UiResult()

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

    data class Supplement(
        val memberName: String,
        val summary: String,
        val cautionText: String?
    ) : UiResult()
}
