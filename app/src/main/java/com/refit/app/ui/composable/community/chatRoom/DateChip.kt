package com.refit.app.ui.composable.community.chatRoom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard

@Composable
fun DateChip(label: String) {
    Surface(
        color = Color(0xFFA1A1A1),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            fontFamily = Pretendard,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
        )
    }
}
