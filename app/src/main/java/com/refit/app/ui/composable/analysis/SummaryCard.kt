package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
            // 제목(고정 디자인 유지)
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
            // ⬇️ 본문: LocalTextStyle 기반으로 색/폰트만 merge
            val bodyStyle = LocalTextStyle.current.merge(
                TextStyle(
                    lineHeight = (LocalTextStyle.current.fontSize.value + 4).sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8A8A8A)
                )
            )
            Text(
                text = summary.ifBlank { "요약 정보가 준비되지 않았어요." },
                style = bodyStyle
            )
        }
    }
}
