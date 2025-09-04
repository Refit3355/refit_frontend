package com.refit.app.data.chatbot.api

import com.refit.app.data.chatbot.model.ProductListRequestDto
import com.refit.app.data.chatbot.model.ProductListResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatbotApi {
    @POST("/chatbot/products")
    @Headers("Requires-Auth: true")
    suspend fun getProducts(
        @Body req: ProductListRequestDto
    ): ProductListResponseDto
}