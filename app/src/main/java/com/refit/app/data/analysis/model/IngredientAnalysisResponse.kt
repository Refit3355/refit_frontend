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

enum class AnalysisStatus {
    OK,
    NO_INGREDIENTS,
    NOT_PRODUCT_LABEL,
    OCR_FAILURE,
    SERVER_ERROR
}

data class FullAnalysisResponse(
    val status: AnalysisStatus? = null,
    val reason: String? = null,
    val suggestion: String? = null,

    val memberName: String? = null,
    val matchRate: Int? = null,
    val risky: List<String>? = null,
    val caution: List<String>? = null,
    val safe: List<String>? = null,
    val riskyText: String? = null,
    val cautionText: String? = null,
    val safeText: String? = null,
    val summary: String? = null,

    val supplementBenefits: String? = null,
    val supplementConditionCautions: String? = null
)