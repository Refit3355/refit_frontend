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
import com.refit.app.ui.theme.Pretendard

/** 단순하게(문단) 전용 섹션 */
@Composable
fun IngredientParagraphSection(
    title: String,
    titleColor: Color,
    icon: @Composable () -> Unit,
    text: String
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.width(8.dp))
            // 제목(고정 디자인 유지)
            Text(
                text = title,
                color = titleColor,
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
                .background(Color(0xFFF0F0F0))
                .padding(14.dp)
        ) {
            // ⬇️ 본문: LocalTextStyle을 기준으로 색/폰트 지정만 merge
            val bodyStyle = LocalTextStyle.current.merge(
                TextStyle(
                    // lineHeight는 현재 폰트크기에 맞춰 살짝 여유
                    lineHeight = (LocalTextStyle.current.fontSize.value + 4).sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8A8A8A)
                )
            )
            Text(text = text, style = bodyStyle)
        }
        Spacer(Modifier.height(18.dp))
    }
}
