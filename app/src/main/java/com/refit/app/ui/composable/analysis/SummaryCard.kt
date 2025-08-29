package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard


@Composable
fun SummaryCard(summary: String, icon: @Composable (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) { it() }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = "전체 요약",
                color = MainPurple,
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F0FA))
                .padding(16.dp)
        ) {
            Text(
                text = summary.ifBlank { "요약 정보가 준비되지 않았어요." },
                color = Color(0xFF8A8A8A),
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}