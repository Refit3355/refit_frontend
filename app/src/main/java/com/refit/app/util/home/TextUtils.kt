package com.refit.app.util.home

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.refit.app.ui.theme.MainPurple

fun highlightText(fullText: String, highlights: List<String>): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        highlights.forEach { keyword ->
            val startIndex = fullText.indexOf(keyword, currentIndex)
            if (startIndex >= 0) {
                // 일반 텍스트
                append(fullText.substring(currentIndex, startIndex))
                // 강조 텍스트
                withStyle(
                    style = SpanStyle(
                        color = MainPurple,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(keyword)
                }
                currentIndex = startIndex + keyword.length
            }
        }
        // 남은 일반 텍스트
        if (currentIndex < fullText.length) {
            append(fullText.substring(currentIndex))
        }
    }
}
