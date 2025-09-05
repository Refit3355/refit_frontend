package com.refit.app.data.chatbot.repository

import com.refit.app.data.chatbot.api.ChatbotApi
import com.refit.app.data.chatbot.model.ProductListRequestDto
import com.refit.app.data.product.model.Product

class ChatbotRepositoryImpl(
    private val api: ChatbotApi
) : ChatbotRepository {

    override suspend fun getProducts(
        bhType: Int,
        effectIds: List<Int>,
        sort: String,
        lastId: Long?,
        limit: Int
    ): Pair<List<Product>, Int> {
        val req = ProductListRequestDto(
            bhType = bhType,
            effectIds = effectIds,
            sort = sort,
            lastId = lastId,
            limit = limit
        )

        val res = api.getProducts(req)

        val items = res.items.map { dto ->
            Product(
                id = dto.productId,
                image = dto.thumbnailUrl,
                brand = dto.brandName,
                name = dto.productName,
                discountRate = dto.discountRate,
                price = dto.originalPrice.toInt(),
                discountedPrice = dto.discountedPrice.toInt()
            )
        }

        return items to res.totalCount
    }
}
