package com.refit.app.data.analysis.modelAndView

import com.refit.app.data.analysis.model.FullAnalysisResponse

private fun String?.nn() = this?.trim().orEmpty()
private fun List<String>?.nn() = this ?: emptyList()

fun FullAnalysisResponse.toUiResult(
    memberNameResolved: String,
    productType: ProductTypeUi
): UiResult {
    val nothing = listOf(summary, riskyText, cautionText, safeText).all { it.isNullOrBlank() } &&
            risky.isNullOrEmpty() && caution.isNullOrEmpty() && safe.isNullOrEmpty() &&
            supplementBenefits.isNullOrEmpty() && supplementConditionCautions.isNullOrEmpty()

    if (nothing) return UiResult.Empty

    return when (productType) {
        ProductTypeUi.BEAUTY -> UiResult.Cosmetic(
            memberName = memberNameResolved,
            matchRate = (matchRate ?: 0).coerceIn(0, 100),
            risky = risky.nn(),
            caution = caution.nn(),
            safe = safe.nn(),
            riskyText = riskyText?.trim().takeUnless { it.isNullOrBlank() },
            cautionText = cautionText?.trim().takeUnless { it.isNullOrBlank() },
            safeText = safeText?.trim().takeUnless { it.isNullOrBlank() },
            summary = summary.nn()
        )
        ProductTypeUi.HEALTH -> {
            val cautionMerged = (cautionText.nn()).ifBlank { riskyText.nn() }.ifBlank { null }
            UiResult.Supplement(
                memberName = memberNameResolved,
                summary = summary.nn(),
                cautionText = cautionMerged
            )
        }
    }
}