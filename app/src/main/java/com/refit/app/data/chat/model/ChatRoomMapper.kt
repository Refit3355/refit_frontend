package com.refit.app.data.chat.model

import java.time.*
import java.time.format.DateTimeFormatter

private val KST = ZoneId.of("Asia/Seoul")

private fun parseToOffsetDateTime(raw: String): OffsetDateTime? {
    // 1) 오프셋/UTC 표기(+09:00, Z)가 있으면 그대로 파싱
    runCatching { return OffsetDateTime.parse(raw, DateTimeFormatter.ISO_OFFSET_DATE_TIME) }

    // 2) 오프셋이 없으면 LocalDateTime으로 파싱 후 KST 부여
    return runCatching {
        val ldt = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ldt.atZone(KST).toOffsetDateTime()
    }.getOrNull()
}

fun ChatRoomDto.toDomain(): ChatRoom =
    ChatRoom(
        categoryId = categoryId,
        categoryName = categoryName,
        lastChatId = lastChatId,
        lastMessage = lastMessage,
        lastAt = lastAt?.let { parseToOffsetDateTime(it) }
    )
