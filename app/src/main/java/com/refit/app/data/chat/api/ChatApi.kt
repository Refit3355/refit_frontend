package com.refit.app.data.chat.api

import com.refit.app.data.chat.model.ChatRoomDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ChatApi {
    @GET("chat/rooms")
    @Headers("Requires-Auth: true")
    suspend fun getChatRooms(
        @Query("tab") tab: String? = null
    ): List<ChatRoomDto>
}