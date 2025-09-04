package com.refit.app.data.analysis.modelAndView

import com.refit.app.data.analysis.model.AnalysisStatus
import com.refit.app.data.analysis.model.FullAnalysisResponse

private fun String?.nn() = this?.trim().orEmpty()
private fun List<String>?.nn() = this ?: emptyList()

private fun blockedTitle(status: AnalysisStatus?): String = when (status) {
    AnalysisStatus.NO_INGREDIENTS     -> "성분을 찾지 못했어요"
    AnalysisStatus.NOT_PRODUCT_LABEL  -> "제품 라벨이 아니에요"
    AnalysisStatus.OCR_FAILURE        -> "문자를 읽지 못했어요"
    AnalysisStatus.SERVER_ERROR       -> "분석 중 오류가 발생했어요"
    else                              -> "분석이 불가합니다"
}

fun FullAnalysisResponse.toUiResult(
    memberNameResolved: String,
    productType: ProductTypeUi
): UiResult {
    if (status != null && status != AnalysisStatus.OK) {
        val msg = buildString {
            if (!suggestion.isNullOrBlank()) append(suggestion!!.trim())
            if (!reason.isNullOrBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("사유: ").append(reason!!.trim())
            }
        }.let { s ->
            if (s.isBlank()) "이미지가 흐리거나 라벨 영역이 잘 보이지 않아요. 다시 촬영해 주세요."
            else s
        }
        return UiResult.Blocked(
            title = blockedTitle(status),
            message = "이미지를 다시 한번 확인해주세요."
        )
    }

    return when (productType) {
        ProductTypeUi.BEAUTY -> {
            val nothing = listOf(summary, riskyText, cautionText, safeText).all { it.isNullOrBlank() } &&
                    risky.isNullOrEmpty() && caution.isNullOrEmpty() && safe.isNullOrEmpty()
            if (nothing) return UiResult.Empty

            UiResult.Cosmetic(
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
        }
        ProductTypeUi.HEALTH -> {
            val benefits = supplementBenefits
                ?.trim()
                ?.takeUnless { it.isBlank() }
                ?: summary.nn()

            val cautions = supplementConditionCautions
                ?.trim()
                ?.takeUnless { it.isBlank() }
                ?: cautionText?.trim().takeUnless { it.isNullOrBlank() }
                ?: riskyText?.trim().takeUnless { it.isNullOrBlank() }
                ?: null

            val nothing = benefits.isBlank() && cautions.isNullOrBlank()
            if (nothing) return UiResult.Empty

            UiResult.Supplement(
                memberName = memberNameResolved,
                summary = benefits,
                cautionText = cautions
            )
        }

    }
}
