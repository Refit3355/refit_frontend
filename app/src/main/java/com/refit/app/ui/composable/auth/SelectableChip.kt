package com.refit.app.ui.composable.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MainPurple else Color.White
    val fg = if (selected) Color.White else Color(0xFF9E9E9E)

    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(24.dp),
        border = if (selected) null else BorderStroke(2.dp, Color.LightGray),
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                fontFamily = Pretendard,
            ),
            color = fg
        )
    }
}