package com.refit.app.data.product.api

import com.refit.app.data.product.model.RecommendationResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductRecommendationApi {
    @GET("products/recommendation/{type}")
    @Headers("Requires-Auth: true")
    suspend fun getRecommendations(
        @Path("type") type: Int, // 0: 전체, 1: 뷰티, 2: 헤어, 3: 건강기능식품
        @Query("limit") limit: Int = 10
    ): RecommendationResponse
}
