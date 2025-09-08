package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard

@Composable
fun SectionCard(
    title: String,
    titleColor: Color,
    titleSize: Int = 18,
    icon: @Composable (() -> Unit)? = null,
    body: String,
    bodyColor: Color = Color(0xFF2B2B2B),
    bodySize: Int = 14
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke()
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                color = titleColor,
                fontSize = titleSize.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.height(8.dp))
        Surface(
            color = Color(0xFFF4EEF8),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val effectiveFontSize =
                if (LocalTextStyle.current.fontSize.isUnspecified) bodySize.sp
                else LocalTextStyle.current.fontSize

            Text(
                text = body,
                modifier = Modifier.padding(16.dp),
                color = bodyColor,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = effectiveFontSize,
            )
        }
    }
}
