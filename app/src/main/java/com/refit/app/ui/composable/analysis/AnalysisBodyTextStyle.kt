package com.refit.app.ui.composable.analysis

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard

@Composable
fun AnalysisBodyTextStyle(): TextStyle {
    val base = LocalTextStyle.current
    return base.merge(
        TextStyle(
            lineHeight = (base.fontSize.value + 4).sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8A8A8A)
        )
    )
}
