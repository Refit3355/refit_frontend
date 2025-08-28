package com.refit.app.data.chat.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val chatId: Long? = null,
    val categoryId: Long,
    val memberId: Long,
    val nickname: String? = null,
    val productId: Long?,
    val message: String,
    val profileUrl: String?,
    val createdAt: String
)