package com.refit.app.ui.composable.home.version2

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlin.math.max


@SuppressLint("Range")
@Composable
fun StepsCard(
    nickname: String,
    steps: Long,
    avg: Int = 5600,
    goal: Int = 9100,
    mascot: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    val remain = (goal - steps).coerceAtLeast(0)
    // 0f..1f 사이로 보정 + Float 캐스팅 명확화
    val rawProgress = (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    // 애니메이션으로 자연스럽게
    val animatedProgress by animateFloatAsState(targetValue = rawProgress, label = "stepsProgress")
    // 너무 작을 때도 보이도록 최소 폭(예: 4%) 보장
    val displayProgress = if (animatedProgress in 0f..0.04f && steps > 0) 0.04f else animatedProgress

    val epsilon = 1e-6f
    val avgRatio = (avg.toFloat() / goal.toFloat()).coerceIn(0.05f, 0.95f)

    val goalReached = steps >= goal
    val remainSteps = (goal - steps).coerceAtLeast(0)
    val overAvg     = steps >= avg
    val nearGoal    = !goalReached && remainSteps <= 500

    val goalMsg = buildAnnotatedString {
        when {
            goalReached -> {
                append("오늘 목표를 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.SemiBold)) {
                    append("달성")
                }
                append("했어요 🎉")
            }
            nearGoal -> {
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.SemiBold)) {
                    append("%,d".format(remainSteps))
                }
                append("걸음만 더 걸으면 목표 달성 🎯")
            }
            overAvg -> {
                append("평균을 넘었어요! 목표까지 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.SemiBold)) {
                    append("%,d".format(remainSteps))
                }
                append("걸음 🚶")
            }
            steps == 0L -> {
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.SemiBold)) {
                    append("가벼운 산책")
                }
                append("부터 시작해 볼까요? 🌱")
            }
            else -> {
                append("꾸준히 걸으면 곧 목표에 다가갈 수 있어요! ✨")
            }
        }
    }

    MyInfoCardFrame(
        title = {
            Column {
                Text(
                    buildAnnotatedString {
                        append("반가워요, ")
                        withStyle(
                            SpanStyle(
                                color = MainPurple,
                                fontWeight = FontWeight(600)
                            )
                        ) {
                            append(nickname)
                        }
                        append("님")
                    },
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(600),
                    fontSize = 20.sp,
                    color = Color(0xFF3A3A3A)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    buildAnnotatedString {
                        append("오늘 하루 ")
                        withStyle(
                            SpanStyle(
                                color = MainPurple,
                                fontWeight = FontWeight(600)
                            )
                        ) {
                            append("%,d".format(steps))
                        }
                        append("걸음 \n걸었어요!")
                    },
                    fontSize = 20.sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF3A3A3A)
                )
            }
        },
        mascot = mascot,
        bottomContent = {
            Column {
                Text(
                    goalMsg,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    fontSize = 15.sp,
                    color = Color(0xFF3A3A3A)
                )
                Spacer(Modifier.height(8.dp))

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

                Spacer(Modifier.height(6.dp))

                // ===== 마커 & 라벨 (바 아래 정렬) =====
                Box(modifier = Modifier.fillMaxWidth()) {

                    // 평균 마커
                    Row(Modifier.fillMaxWidth()) {
                        val leftW  = max(epsilon, avgRatio)
                        val rightW = max(epsilon, 1f - avgRatio)

                        Spacer(Modifier.weight(leftW))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("평균 ${"%,d".format(avg)}",
                                fontSize = 12.sp,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF6B6B6B))
                        }
                        Spacer(Modifier.weight(rightW))
                    }

                    // 권장(끝) 마커 — 끝 정렬로 처리(0 weight 회피)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("권장 ${"%,d".format(goal)}",
                                fontSize = 13.sp,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF6B6B6B))
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
