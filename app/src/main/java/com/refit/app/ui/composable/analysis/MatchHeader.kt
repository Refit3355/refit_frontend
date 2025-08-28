package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

private val PurpleSoft = Color(0xFFF3ECFF)

@Composable
fun MatchHeader(memberName: String, matchRate: Int) {
    val gradient = Brush.verticalGradient(listOf(PurpleSoft, Color.White))
    Box(
        Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = buildAnnotatedString {
            append("요청하신 이미지 분석 결과,\n")
            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append(memberName) }
            append("님과 해당 상품의\n")
            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.ExtraBold)) { append("매칭률은 $matchRate% ") }
            append("입니다.")
        }
        Text(text = text,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontFamily = Pretendard,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)
    }
}
