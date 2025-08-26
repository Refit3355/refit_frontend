package com.refit.app.data.chat.usecase

import com.refit.app.data.chat.model.ChatRoom
import com.refit.app.data.chat.repository.ChatRepository
import com.refit.app.ui.composable.community.CommunityCategory

class GetChatRoomsUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(category: CommunityCategory): List<ChatRoom> =
        repository.getChatRooms(category)
}