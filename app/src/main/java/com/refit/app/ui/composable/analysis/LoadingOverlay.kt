package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.delay
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

@Composable
fun LoadingOverlay(visible: Boolean) {
    if (!visible) return

    val messages = listOf(
        R.drawable.jellbbo_default    to "Refit AI가 분석 중..",
        R.drawable.jellbbo_doctor     to "당신의 맞춤별 성분을 분석해요.",
        R.drawable.jellbbo_sunny      to "성분을 꼼꼼히 분석하고 있어요.",
        R.drawable.jellbbo_walk       to "거의 다 됐어요!"
    )
    var index by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            index = (index + 1) % messages.size
        }
    }
    val (iconRes, textMsg) = messages[index]

    val pulse = rememberInfiniteTransition(label = "pulse")
        .animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAnim"
        ).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F3FC)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                strokeWidth = 6.dp,
                color = Color(0xFF9C4DCC),
                trackColor = Color(0xFFE1BEE7),
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.height(28.dp))

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(170.dp)
                    .scale(pulse)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = textMsg,
                color = Color(0xFF6A1B9A),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center,
                fontFamily = Pretendard,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}


