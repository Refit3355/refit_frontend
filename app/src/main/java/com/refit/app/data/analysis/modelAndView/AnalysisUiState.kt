package com.refit.app.data.analysis.modelAndView

data class AnalysisUiState(
    val memberName: String = "",
    val matchRate: Int = 0,
    val risky: List<String> = emptyList(),
    val caution: List<String> = emptyList(),
    val safe: List<String> = emptyList(),
    val riskyText: String = "",
    val cautionText: String = "",
    val safeText: String = "",
    val summary: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)