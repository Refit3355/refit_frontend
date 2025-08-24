package com.refit.app.data.me.repository

import com.refit.app.data.me.api.MeApi
import com.refit.app.data.local.combination.model.CombinationsResponse
import com.refit.app.data.me.model.LikeProductDto
import com.refit.app.data.me.model.LikeRequest
import com.refit.app.data.me.model.OrdersResponse
import com.refit.app.data.product.model.Product
import com.refit.app.network.RetrofitInstance

private fun LikeProductDto.toDomain() = Product(
    id = id,
    image = thumbnailUrl,
    brand = brandName,
    name = productName,
    price = price,
    discountedPrice = discountedPrice,
    discountRate = discountRate
)

class MeRepository(
    private val api: MeApi = RetrofitInstance.create(MeApi::class.java)
) {
    suspend fun getLikedProducts(ids: Set<Long>): Result<List<Product>> = runCatching {
        if (ids.isEmpty()) return@runCatching emptyList<Product>()
        val res = api.getLikedProducts(LikeRequest(ids.toList()))
        res.items.map { it.toDomain() }
    }

    suspend fun getOrders(): Result<OrdersResponse> = runCatching {
        api.getOrders()
    }

    suspend fun getCreatedCombinations(): Result<CombinationsResponse> = runCatching {
        api.getCreatedCombinations()
    }
}