package com.refit.app.data.me.repository

import com.refit.app.data.me.api.MeApi
import com.refit.app.data.combination.model.CombinationsResponse
import com.refit.app.data.me.model.LikeProductDto
import com.refit.app.data.me.model.LikeRequest
import com.refit.app.data.me.model.OrdersResponse
import com.refit.app.data.me.model.UpdateOrderStatusResponse
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
    private val meApi: MeApi = RetrofitInstance.create(MeApi::class.java),
) {
    // 찜한 상품 불러오기
    suspend fun getLikedProducts(ids: Set<Long>): Result<List<Product>> = runCatching {
        if (ids.isEmpty()) return@runCatching emptyList<Product>()
        val res = meApi.getLikedProducts(LikeRequest(ids.toList()))
        res.items.map { it.toDomain() }
    }

    // 주문 내역 불러오기
    suspend fun getOrders(): Result<OrdersResponse> = runCatching {
        meApi.getOrders()
    }

    // 내가 만든 조합 불러오기
    suspend fun getCreatedCombinations(): Result<CombinationsResponse> = runCatching {
        meApi.getCreatedCombinations()
    }

    // 교환 신청
    suspend fun requestExchange(orderItemId: Long): Result<UpdateOrderStatusResponse> = runCatching {
        meApi.requestExchange(orderItemId)
    }

    // 반품 신청
    suspend fun requestReturn(orderItemId: Long): Result<UpdateOrderStatusResponse> = runCatching {
        meApi.requestReturn(orderItemId)
    }
}
