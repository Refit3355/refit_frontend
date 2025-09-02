package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OverviewCarouselMessage(
    onNext: (String) -> Unit,
    resetKey: Any
) {
    // 말풍선과 구분되는 “별도 메시지 블록” 느낌의 래퍼 (배경=투명, 여백만)
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 46.dp, end = 12.dp, bottom = 6.dp)
    ) {
        ServiceOverviewCarousel(onNext = onNext, resetKey = resetKey)
    }
}
