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

data class FullAnalysisResponse(
    val memberName: String,
    val matchRate: Int,
    val risky: List<String>,
    val caution: List<String>,
    val safe: List<String>,
    val riskyText: String,
    val cautionText: String,
    val safeText: String,
    val summary: String
)