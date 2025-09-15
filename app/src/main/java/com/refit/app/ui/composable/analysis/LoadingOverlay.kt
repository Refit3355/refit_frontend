package com.refit.app.ui.composable.analysis

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.refit.app.ui.theme.Pretendard
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun CuteLoadingOverlay(
    visible: Boolean,
    gifResId: Int,
    gifSize: Dp = 170.dp,
    blockTouches: Boolean = true,
    messages: List<String> = listOf(
        "Refit AI가 분석 중이에요",
        "당신에게 맞는 성분을 찾고 있어요",
        "조금만 기다려 주세요",
        "거의 다 됐어요!"
    ),
    messageIntervalMs: Long = 3000L
) {
    if (!visible) return

    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()
    }

    val blocker = if (blockTouches) {
        Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* consume */ }
    } else Modifier.fillMaxSize()

    var idx by remember { mutableStateOf(0) }
    LaunchedEffect(messages) {
        while (true) {
            kotlinx.coroutines.delay(messageIntervalMs)
            idx = (idx + 1) % messages.size
        }
    }

    Box(
        modifier = blocker.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = gifResId,
                imageLoader = imageLoader,
                contentDescription = null,
                modifier = Modifier.size(gifSize)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = messages[idx],
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

