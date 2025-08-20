package com.refit.app.data.product.repository

import com.refit.app.data.product.api.ProductRecommendationApi
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.model.toDomain
import com.refit.app.network.RetrofitInstance

class RecommendationRepository(
    private val api: ProductRecommendationApi = RetrofitInstance.create(ProductRecommendationApi::class.java)
) {
    suspend fun fetchRecommendations(type: Int, limit: Int = 10): Result<List<Product>> =
        runCatching {
            val res = api.getRecommendations(type, limit)
            res.items.map { dto -> dto.toDomain() }
        }
}
