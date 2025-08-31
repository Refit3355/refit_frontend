package com.refit.app.data.chat.model

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ChatMessage(
    val chatId: Long,
    val categoryId: Long,
    val memberId: Long,
    val nickname: String,
    val productId: Long?,
    val message: String,
    val profileUrl: String?,
    val createdAt: OffsetDateTime,
    val product: ProductSnippet? = null
)