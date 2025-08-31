package com.refit.app.ui.composable.community.chatRoom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.chat.model.ChatMessage
import com.refit.app.ui.theme.Pretendard

@Composable
fun MyMessageBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // 왼쪽 아바타 자리 비우기(좌우 균형)
        Spacer(Modifier.size(36.dp))
        Spacer(Modifier.weight(1f))

        // 오른쪽 끝에서 "시간 ─ 버블(내용)" 순서
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = msg.createdAt.koreanTime(),
                fontFamily = Pretendard,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 6.dp)
            )

            Surface(
                color = Color(0xFFEEEEEE),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 8.dp,
                    bottomStart = 16.dp, bottomEnd = 16.dp
                ),
                modifier = Modifier.widthIn(max = 240.dp)
            ) {
                Text(
                    text = msg.message,
                    fontFamily = Pretendard,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }
    }
}
