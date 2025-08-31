package com.refit.app.data.chat.usecase

import com.refit.app.data.chat.model.ChatPage
import com.refit.app.data.chat.repository.ChatRepository

class GetChatMessagesPageUseCase(
    private val repo: ChatRepository
) {
    suspend operator fun invoke(
        categoryId: Long,
        size: Int,
        cursor: String?
    ): ChatPage = repo.getMessages(categoryId, size, cursor)
}