package com.refit.app.data.chat.model

import java.time.OffsetDateTime

data class ChatRoom(
    val categoryId: Long,
    val categoryName: String,
    val lastChatId: Long?,
    val lastMessage: String?,
    val lastAt: OffsetDateTime?
)