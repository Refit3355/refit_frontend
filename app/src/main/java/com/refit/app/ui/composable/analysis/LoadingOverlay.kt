package com.refit.app.ui.composable.analysis

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingOverlay(visible: Boolean) {
    if (!visible) return

    val brand = Color(0xFF5F0080)
    val brandLight = Color(0xFFD17DFC)
    val brandTrack = brand.copy(alpha = 0.10f)
    val bgScrim = Color(0xFF0F0A14)
    val cardBg = Color.White.copy(alpha = 0.92f)
    val cardBorder = Color(0x1A000000)

    val cardWidth = 340.dp
    val contentWidth = cardWidth - 40.dp

    val messages = listOf(
        R.drawable.jellbbo_default to "Refit AI가 분석 중…",
        R.drawable.jellbbo_doctor  to "당신의 맞춤 성분을 살피고 있어요",
        R.drawable.jellbbo_sunny   to "조금만 기다려주세요. 꼼꼼히 확인 중!",
        R.drawable.jellbbo_walk    to "거의 다 됐어요!"
    )
    var idx by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            idx = (idx + 1) % messages.size
        }
    }

    val rotation by rememberInfiniteTransition(label = "spinner")
        .animateFloat(0f, 360f, infiniteRepeatable(tween(1100, easing = LinearEasing)), label = "deg")
    val pulse by rememberInfiniteTransition(label = "pulse")
        .animateFloat(0.96f, 1.04f, infiniteRepeatable(tween(820, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "scale")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgScrim.copy(alpha = 0.26f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .requiredWidth(cardWidth)
                .wrapContentHeight(),
            color = cardBg,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 0.dp,
            shadowElevation = 14.dp,
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 스피너
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.matchParentSize()) {
                        val strokeWidthPx = 6.dp.toPx()
                        val half = strokeWidthPx / 2f
                        val rect = Rect(half, half, size.width - half, size.height - half)

                        drawArc(
                            color = brandTrack,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )
                        rotate(rotation) {
                            val arcPath = Path().apply { addArc(rect, 0f, 280f) }
                            val comet = Brush.sweepGradient(
                                0.00f to brandLight.copy(alpha = 0.00f),
                                0.10f to brandLight.copy(alpha = 0.35f),
                                0.50f to brand,
                                0.90f to brandLight.copy(alpha = 0.35f),
                                1.00f to brandLight.copy(alpha = 0.00f)
                            )
                            drawPath(path = arcPath, brush = comet, style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round))
                            val center = Offset(size.width - half, size.height / 2f)
                            val glowRadius = 18.dp.toPx()
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(brand.copy(alpha = 0.30f), Color.Transparent),
                                    center = center,
                                    radius = glowRadius
                                ),
                                radius = glowRadius
                            )
                        }
                        drawCircle(
                            color = Color.White.copy(alpha = 0.92f),
                            radius = (size.minDimension / 2f) - strokeWidthPx * 1.2f
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Box(modifier = Modifier.requiredWidth(contentWidth), contentAlignment = Alignment.Center) {
                    AnimatedContent(
                        targetState = messages[idx].first,
                        transitionSpec = { fadeIn(tween(250)) with fadeOut(tween(200)) },
                        label = "icon-xfade"
                    ) { resId ->
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).scale(pulse)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Box(modifier = Modifier.requiredWidth(contentWidth), contentAlignment = Alignment.Center) {
                    AnimatedContent(
                        targetState = messages[idx].second,
                        transitionSpec = { fadeIn(tween(200)) with fadeOut(tween(160)) },
                        label = "text-xfade"
                    ) { text ->
                        androidx.compose.material3.Text(
                            text = text,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            color = brand,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // 서브카피
                var dotCount by remember { mutableStateOf(0) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(300)
                        dotCount = (dotCount + 1) % 4
                    }
                }
                val trailing = ".".repeat(dotCount)

                // ✅ 서브카피도 같은 폭으로 고정
                Box(modifier = Modifier.requiredWidth(contentWidth), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.Text(
                        text = "안정적으로 처리하고 있어요$trailing",
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF6B6B6B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(0.9f)
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
