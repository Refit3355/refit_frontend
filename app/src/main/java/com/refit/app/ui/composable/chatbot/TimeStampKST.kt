package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimeStampKST(
    at: Long,
    alignStart: Boolean // 봇(좌)=true, 사용자(우)=false
) {
    val text = remember(at) {
        java.time.Instant.ofEpochMilli(at)
            .atZone(java.time.ZoneId.of("Asia/Seoul"))
            .format(
                java.time.format.DateTimeFormatter.ofPattern("a hh:mm", java.util.Locale.KOREA)
            )
    }

    if (alignStart) {
        // 봇: 아바타 폭만큼 들여쓰기
        Text(
            text = text,
            color = Color(0x99000000),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 62.dp, top = 2.dp, bottom = 4.dp)
        )
    } else {
        // 사용자: 우측 정렬
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            Text(
                text = text,
                color = Color(0x99000000),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(end = 12.dp, top = 2.dp, bottom = 4.dp)
            )
        }
    }
}
