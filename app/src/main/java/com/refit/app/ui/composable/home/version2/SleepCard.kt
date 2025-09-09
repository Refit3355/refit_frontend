package com.refit.app.ui.composable.home.version2

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.ui.theme.PretendardVariable
import kotlin.math.max

@SuppressLint("Range")
@Composable
fun SleepCard(
    sleepMinutes: Long,                 // 실제 수면(분)
    mascot: @Composable () -> Unit,
    goalMinutes: Int = 8 * 60,          // 권장 8시간
    avgMinutes: Int = 6 * 60 + 30,            // 평균(예시) 6시간
    onClick: () -> Unit = {}
) {
    // 포맷터
    fun hm(min: Int): String = "%d시간 %d분".format(min / 60, min % 60)

    // 진행률(애니메이션 + 최소 표시폭)
    val rawProgress = (sleepMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = rawProgress, label = "sleepProgress")
    val displayProgress = if (animatedProgress in 0f..0.04f && sleepMinutes > 0) 0.04f else animatedProgress

    // 상태 판별
    val goalReached = sleepMinutes >= goalMinutes
    val nearGoal    = !goalReached && (goalMinutes - sleepMinutes) <= 30      // 30분 이내
    val lowSleep    = sleepMinutes in 1..(5 * 60 - 1)                          // 5시간 미만
    val zeroSleep   = sleepMinutes == 0L

    // 하단 메시지(짧고 아이콘 포함, 숫자 하이라이트)
    val msg = buildAnnotatedString {
        when {
            zeroSleep -> {
                append("오늘 밤은 편안한 잠을 준비해 보세요 🌙")
            }
            goalReached -> {
                append("몸과 마음이 잘 회복되었을 거예요 🎉")
            }
            nearGoal -> {
                append("푹 주무셨네요. 몸이 한결 가벼우실 거예요 😴")
            }
            lowSleep -> {
                append("오늘은 일찍 잠자리에 들어보세요 💤")
            }
            else -> {
                append("조금 더 쉬면 더 좋은 컨디션을 유지할 수 있어요 ✨")
            }
        }
    }

    // 평균/권장 라벨 위치(프로그레스 바 아래)
    val epsilon = 1e-6f
    val avgRatio = (avgMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0.05f, 0.95f)

    MyInfoCardFrame(
        title = {
            Column {
                Text(
                    buildAnnotatedString {
                        append("편안한 밤 보내셨나요?\n")
                        append("어젯밤 수면 시간은\n")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight(600))) {
                            append(hm(sleepMinutes.toInt()))
                        }
                        append("이에요")
                    },
                    style = TextStyle(
                        fontFamily = PretendardVariable,
                        fontWeight = FontWeight(600)
                    ),
                    fontSize = 20.sp,
                    color = Color(0xFF3A3A3A)
                )
            }
        },
        mascot = mascot,
        bottomContent = {
            Column {
                Text(
                    msg,
                    style = TextStyle(
                        fontFamily = PretendardVariable,
                        fontWeight = FontWeight(580)
                    ),
                    fontSize = 15.sp,
                    color = Color(0xFF3A3A3A)
                )

                Spacer(Modifier.height(13.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE6E2EC))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = displayProgress)
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MainPurple)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // 평균/권장 라벨 (막대 아래, 비율 위치)
                Box(modifier = Modifier.fillMaxWidth()) {
                    // 평균 라벨
                    Row(Modifier.fillMaxWidth()) {
                        val leftW  = max(epsilon, avgRatio)
                        val rightW = max(epsilon, 1f - avgRatio)

                        Spacer(Modifier.weight(leftW))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "평균 ${avgMinutes / 60}H",
                                fontSize = 12.sp,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF6B6B6B)
                            )
                        }
                        Spacer(Modifier.weight(rightW))
                    }

                    // 권장 라벨(오른쪽 끝)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "권장 ${goalMinutes / 60}H",
                                fontSize = 12.sp,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF6B6B6B)
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true)
        ) { onClick() }
    )
}
