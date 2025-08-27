package com.refit.app.data.chat.model

import java.time.*
import java.time.format.DateTimeFormatter

private val KST = ZoneId.of("Asia/Seoul")

fun ChatMessageDto.toDomain(): ChatMessage {
    // 오프셋 없는 ISO_LOCAL_DATE_TIME → KST 부여
    val ldt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val odt = ldt.atZone(KST).toOffsetDateTime()
    return ChatMessage(
        chatId = chatId,
        categoryId = categoryId,
        memberId = memberId,
        nickname = nickname,
        productId = productId,
        message = message,
        profileUrl = profileUrl,
        createdAt = odt
    )
}