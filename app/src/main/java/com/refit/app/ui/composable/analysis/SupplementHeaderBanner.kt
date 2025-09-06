package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun SupplementHeaderBanner(
    memberName: String,
    nameColor: Color = MainPurple,
    nameSize: Int = 22,
    baseSize: Int = 20
) {
    val name = memberName.ifBlank { "고객" }

    val text = buildAnnotatedString {
        withStyle(
            style = ParagraphStyle(textAlign = TextAlign.Center)
        ) {
            withStyle(
                SpanStyle(
                    color = nameColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = nameSize.sp,
                    fontFamily = Pretendard
                )
            ) {
                append(name)
            }

            withStyle(
                SpanStyle(
                    fontSize = baseSize.sp,
                    fontFamily = Pretendard
                )
            ) {
                append("님이 \n요청하신 이미지 분석 결과입니다.")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(Color(0xFFF1EBF7))
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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