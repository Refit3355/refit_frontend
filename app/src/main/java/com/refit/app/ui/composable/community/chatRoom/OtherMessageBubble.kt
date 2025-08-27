package com.refit.app.ui.composable.community.chatRoom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.chat.model.ChatMessage
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun OtherMessageBubble(
    msg: ChatMessage,
    showAvatarAndName: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 왼쪽 프로필
        if (showAvatarAndName) {
            Avatar(profileUrl = msg.profileUrl)
        } else {
            Spacer(Modifier.size(36.dp)) // 아바타 자리 유지(정렬 안정)
        }
        Spacer(Modifier.width(8.dp))

        // 닉네임 + (버블 ─ 시간) 세트
        Column(Modifier.weight(1f)) {
            if (showAvatarAndName) {
                Text(
                    text = msg.nickname,
                    fontFamily = Pretendard,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
            }

            // 말풍선과 시간을 같은 Row에 두고 Bottom 정렬 → 시간은 버블 오른쪽 하단에
            Row(verticalAlignment = Alignment.Bottom) {
                Surface(
                    color = LightPurple,
                    shape = RoundedCornerShape(
                        topStart = 8.dp, topEnd = 16.dp,
                        bottomEnd = 16.dp, bottomStart = 16.dp
                    ),
                    modifier = Modifier.widthIn(max = 240.dp)
                ) {
                    Text(
                        text = msg.message,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }

                Spacer(Modifier.width(6.dp))
                Text(
                    text = msg.createdAt.koreanTime(),
                    fontFamily = Pretendard,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 오른쪽 여백(좌측형 버블에서 균형 맞춤)
        Spacer(Modifier.width(12.dp))
    }
}
