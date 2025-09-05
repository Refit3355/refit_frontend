package com.refit.app.data.analysis.modelAndView

fun UiResult.isEmptyResult(): Boolean = when (this) {
    is UiResult.Cosmetic -> {
        val listsEmpty = risky.isEmpty() && caution.isEmpty() && safe.isEmpty()
        val textsBlank = riskyText.isNullOrBlank() && cautionText.isNullOrBlank() && safeText.isNullOrBlank()
        val summaryBlank = summary.isBlank()
        listsEmpty && textsBlank && summaryBlank
    }
    is UiResult.Supplement -> summary.isBlank() && cautionText.isNullOrBlank()
    is UiResult.Blocked -> false
    else -> true
}
