package com.refit.app.ui.composable.health.chartHeader

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.LightGreyText
import com.refit.app.ui.theme.Pretendard

@Composable
fun TitleLine(
    @DrawableRes iconRes: Int,
    title: String,
    titleColor: Color = Color(0xFF222222)
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = titleColor,
                fontFamily = Pretendard,
                fontWeight = FontWeight(600),
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun InfoCallout(
    text: AnnotatedString,
    bg: Color = LightGreyText.copy(alpha = 0.1f)
) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(7.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF4A4A4A),
                    lineHeight = 18.sp
                ),
                fontFamily = Pretendard,
                fontSize = 14.sp
            )
        }
    }
}