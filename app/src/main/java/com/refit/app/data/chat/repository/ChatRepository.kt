package com.refit.app.data.chat.repository

import com.refit.app.data.chat.api.ChatApi
import com.refit.app.data.chat.model.ChatPage
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

    suspend fun getMessages(categoryId: Long, size: Int, cursor: String?): ChatPage {
        val res = api.getMessages(categoryId = categoryId, size = size, cursor = cursor)
        return ChatPage(
            items = res.items.map { it.toDomain() },
            nextCursor = res.nextCursor,
            hasNext = res.hasNext
        )
    }
}