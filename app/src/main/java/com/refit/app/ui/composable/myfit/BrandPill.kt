package com.refit.app.ui.composable.myfit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.GreyText
import com.refit.app.ui.theme.Pretendard

@Composable
fun BrandPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.sp,
            fontFamily = Pretendard,
            lineHeight = 12.sp
        ),
        color = GreyText,
        modifier = modifier
    )
}