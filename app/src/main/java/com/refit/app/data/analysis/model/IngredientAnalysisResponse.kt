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
    val memberName: String? = null,
    val matchRate: Int? = null,
    val risky: List<String>? = null,
    val caution: List<String>? = null,
    val safe: List<String>? = null,
    val riskyText: String? = null,
    val cautionText: String? = null,
    val safeText: String? = null,
    val summary: String? = null,

    val supplementBenefits: List<String>? = null,
    val supplementConditionCautions: List<String>? = null
)