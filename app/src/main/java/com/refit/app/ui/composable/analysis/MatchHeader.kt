package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun MatchHeader(
    memberName: String,
    matchRate: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1EBF7))
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        val text = buildAnnotatedString {
            append("요청하신 이미지 분석 결과,\n")
            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold, fontFamily = Pretendard, fontSize = 18.sp,)) {
                append(if (memberName.isNotBlank()) memberName else "고객")
            }
            append("님과 해당 상품의\n")
            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold, fontFamily = Pretendard, fontSize = 18.sp,)) {
                append("매칭률은 ${matchRate.coerceIn(0,100)}% ")
            }
            append("입니다.")
        }
        Text(
            text = text,
            color = Color(0xFF4B4B4B),
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = Pretendard,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


