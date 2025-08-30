package com.refit.app.ui.composable.community.chatRoom

import com.refit.app.data.chat.model.ChatMessage
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

// 채팅 리스트에 섞어 넣어 렌더링할 아이템 타입
sealed class ChatUiItem {
    data class DateHeader(val date: LocalDate) : ChatUiItem()
    data class Message(
        val data: ChatMessage,
        val isMine: Boolean,
        val showAvatarAndName: Boolean
    ) : ChatUiItem()
}

private val KST = ZoneId.of("Asia/Seoul")

/**
 * messagesAscending: 오래된 -> 최신(오름차순) 리스트를 넘기세요.
 * myMemberId: 내 회원 ID (null이면 모두 상대 메시지로 처리)
 * groupGapMinutes: 같은 보낸 사람이라도 이 분 이상 간격 나면 아바타/닉네임 다시 보여줌
 */
fun buildChatUiItems(
    messagesAscending: List<ChatMessage>,
    myMemberId: Long?,
    groupGapMinutes: Long = 3
): List<ChatUiItem> {
    val items = mutableListOf<ChatUiItem>()

    var lastDate: LocalDate? = null
    var lastSender: Long? = null
    var lastEpochSec: Long? = null

    // 안전하게 오름차순 보장
    val msgs = messagesAscending.sortedBy { it.createdAt }

    for (m in msgs) {
        val date = m.createdAt.atZoneSameInstant(KST).toLocalDate()
        if (date != lastDate) {
            items += ChatUiItem.DateHeader(date)
            lastDate = date
            lastSender = null
            lastEpochSec = null
        }

        val isMine = (myMemberId != null && m.memberId == myMemberId)

        val epoch = m.createdAt.toEpochSecond()
        val timeGapOk = lastEpochSec?.let { (epoch - it) >= Duration.ofMinutes(groupGapMinutes).seconds } ?: true

        val showAvatarAndName = !isMine && (lastSender == null || lastSender != m.memberId || timeGapOk)

        items += ChatUiItem.Message(
            data = m,
            isMine = isMine,
            showAvatarAndName = showAvatarAndName
        )

        lastSender = m.memberId
        lastEpochSec = epoch
    }
    return items
}
