package com.refit.app.data.analysis.model

data class IngredientAnalysisResponse(
    val memberId: Long,
    val totalIngredients: Int,
    val matchRate: Double,
    val safeIngredients: List<String>,
    val cautionIngredients: List<String>,
    val riskyIngredients: List<String>,
    val summary: String
)