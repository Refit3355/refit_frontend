package com.refit.app.data.chat.model

data class ChatMessageDto(
    val chatId: Long,
    val categoryId: Long,
    val memberId: Long,
    val nickname: String,
    val productId: Long?,
    val message: String,
    val profileUrl: String?,
    val createdAt: String
)