package com.refit.app.data.me.api

import com.refit.app.data.local.combination.model.CombinationsResponse
import com.refit.app.data.me.model.LikeRequest
import com.refit.app.data.me.model.LikeResponse
import com.refit.app.data.me.model.OrdersResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface MeApi {
    @POST("products/like")
    @Headers("Requires-Auth: true")
    suspend fun getLikedProducts(@Body body: LikeRequest): LikeResponse

    @GET("me/orders")
    @Headers("Requires-Auth: true")
    suspend fun getOrders(): OrdersResponse

    @GET("me/combinations")
    @Headers("Requires-Auth: true")
    suspend fun getCreatedCombinations(): CombinationsResponse

}