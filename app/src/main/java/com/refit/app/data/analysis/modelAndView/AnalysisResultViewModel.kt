package com.refit.app.data.analysis.modelAndView

import com.refit.app.data.analysis.model.IngredientAnalysisResponse

data class AnalysisUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val memberName: String = "",
    val matchRate: Int = 0,
    val risky: List<String> = emptyList(),
    val caution: List<String> = emptyList(),
    val safe: List<String> = emptyList(),
    val summary: String = ""
)
enum class DetailTab { Detailed, Simple }

interface AnalysisRepository {
    suspend fun getReport(ingredients: List<String>): IngredientAnalysisResponse
}

