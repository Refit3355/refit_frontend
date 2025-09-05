package com.refit.app.data.chatbot.repository

import com.refit.app.data.product.model.Product

interface ChatbotRepository {
    suspend fun getProducts(
        bhType: Int,
        effectIds: List<Int>,
        sort: String = "latest",
        lastId: Long? = null,
        limit: Int = 20
    ): Pair<List<Product>, Int>
}