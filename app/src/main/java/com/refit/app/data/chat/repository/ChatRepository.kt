package com.refit.app.data.chat.repository

import com.refit.app.data.chat.api.ChatApi
import com.refit.app.data.chat.model.ChatRoom
import com.refit.app.data.chat.model.toDomain
import com.refit.app.ui.composable.community.CommunityCategory

class ChatRepository(
    private val api: ChatApi
) {
    suspend fun getChatRooms(category: CommunityCategory): List<ChatRoom> {
        val tab: String? = when (category) {
            CommunityCategory.ALL -> null
            CommunityCategory.BEAUTY -> "beauty"
            CommunityCategory.HEALTH -> "health"
        }
        return api.getChatRooms(tab).map { it.toDomain() }
    }
}