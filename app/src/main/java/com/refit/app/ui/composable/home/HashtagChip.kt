package com.refit.app.ui.composable.home

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun HashtagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFC9BADD))
    ) {
        Text(
            text = text,
            color = MainPurple,
            fontSize = 12.sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight(500),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}
