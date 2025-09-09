package com.refit.app.ui.composable.home.version2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember

@Composable
fun TemperatureCard(
    temperatureC: Int,
    mascot: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    val tipMsg = buildAnnotatedString {
        when {
            temperatureC >= 28 -> {
                append("햇볕이 강해요 ☀️\n")
                append("SPF 50+ 선크림과 수분 보충을 챙겨주세요.")
            }
            temperatureC >= 24 -> {
                append("따뜻한 날씨예요 🌤️\n")
                append("라이트 로션과 선스크린으로 가볍게 케어해요.")
            }
            temperatureC >= 18 -> {
                append("산책하기 좋은 온도예요 👟\n")
                append("가벼운 스트레칭과 수분 섭취를 함께해요.")
            }
            temperatureC >= 10 -> {
                append("살짝 선선해요 🍂\n")
                append("보습제를 챙기고 겹쳐 입으면 좋아요.")
            }
            else -> {
                append("추운 날씨예요 ❄️\n")
                append("고보습 크림과 따뜻한 물로 컨디션 관리하세요.")
            }
        }
    }

    MyInfoCardFrame(
        title = {
            Column {
                Row {
                    Text(
                        "현재 기온 ",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(600),
                        fontSize = 20.sp,
                        color = Color(0xFF111111)
                    )
                    Text(
                        "${temperatureC}℃",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(600),
                        fontSize = 20.sp,
                        color = MainPurple
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "오늘 날씨에 맞는 케어를 준비했어요",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(600),
                    fontSize = 20.sp,
                    color = Color(0xFF3A3A3A)
                )
            }
        },
        mascot = mascot,
        bottomContent = {
            Column {
                Text(
                    "오늘의 케어 팁 \uD83D\uDCA1",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    color = MainPurple
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    tipMsg,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
        },
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true)
        ) { onClick() }
    )
}
