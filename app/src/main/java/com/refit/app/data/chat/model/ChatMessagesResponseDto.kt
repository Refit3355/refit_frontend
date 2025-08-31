package com.refit.app.data.chat.model

data class ChatMessagesResponseDto(
    val items: List<ChatMessageDto>,
    val nextCursor: String?,
    val hasNext: Boolean
)
