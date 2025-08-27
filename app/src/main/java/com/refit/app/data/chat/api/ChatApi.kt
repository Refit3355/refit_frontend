package com.refit.app.data.chat.api

import com.refit.app.data.chat.model.ChatMessagesResponseDto
import com.refit.app.data.chat.model.ChatRoomDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {
    @GET("chat/rooms")
    @Headers("Requires-Auth: true")
    suspend fun getChatRooms(
        @Query("tab") tab: String? = null
    ): List<ChatRoomDto>

    @GET("chat/{categoryId}/messages")
    @Headers("Requires-Auth: true")
    suspend fun getMessages(
        @Path("categoryId") categoryId: Long,
        @Query("size") size: Int = 20,
        @Query("cursor") cursor: String? = null
    ): ChatMessagesResponseDto
}