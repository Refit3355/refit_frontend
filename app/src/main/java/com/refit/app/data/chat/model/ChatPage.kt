package com.refit.app.data.chat.model

data class ChatPage(
    val items: List<ChatMessage>,
    val nextCursor: String?,
    val hasNext: Boolean
)