package com.refit.app.data.chat.model

data class ChatRoomDto(
    val categoryId: Long,
    val categoryName: String,
    val lastChatId: Long?,
    val lastMessage: String?,
    val lastAt: String?
)