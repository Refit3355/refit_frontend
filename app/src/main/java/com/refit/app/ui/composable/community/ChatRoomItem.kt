package com.refit.app.ui.composable.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.data.chat.model.ChatRoom
import com.refit.app.ui.theme.Pretendard
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatRoomItem(
    room: ChatRoom,
    onClick: (ChatRoom) -> Unit
) {
    val timeBadge = remember(room.lastAt) { formatChatTimestamp(room.lastAt) }
    val preview = room.lastMessage ?: "최근 메시지가 없습니다."

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(room) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1) 제일 왼쪽: 채팅 아이콘
        Image(
            painter = painterResource(R.drawable.ic_icon_chat),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(Modifier.width(20.dp))

        // 2) 아이콘 오른쪽: 텍스트 영역(카테고리명/최근 메시지)
        Column(modifier = Modifier.fillMaxWidth()) {
            // 상단 행: 카테고리명(좌) + 시간(우상단)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = room.categoryName,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp

                )
                if (timeBadge != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = timeBadge,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(400),
                        fontSize = 14.sp,
                        color = Color(0xFFB4B4B4)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // 하단: 최근 메시지
            Text(
                text = preview,
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                maxLines = 1,
                color = Color(0xFFB4B4B4)
            )
        }
    }
}

/** 마지막 채팅 시간 포맷:
 *  - 오늘: HH:mm
 *  - 어제: "어제"
 *  - 그 외: yy/MM/dd (예: 25/08/24)
 */
private fun formatChatTimestamp(lastAt: OffsetDateTime?): String? {
    lastAt ?: return null
    val zone = ZoneId.systemDefault()
    val now = ZonedDateTime.now(zone)
    val target = lastAt.atZoneSameInstant(zone)

    val today = now.toLocalDate()
    val targetDate = target.toLocalDate()

    return when {
        targetDate.isEqual(today) -> target.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        targetDate.isEqual(today.minusDays(1)) -> "어제"
        else -> target.format(DateTimeFormatter.ofPattern("yy/MM/dd"))
    }
}
