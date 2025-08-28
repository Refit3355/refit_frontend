package com.refit.app.data.combination.api

import com.refit.app.data.combination.model.CombinationDetailResponse
import com.refit.app.data.combination.model.CombinationLikeResponse
import com.refit.app.data.combination.model.LikedCombinationRequest
import com.refit.app.data.combination.model.CombinationsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CombinationApi {
    @POST("combinations/like")
    @Headers("Requires-Auth: true")
    suspend fun getLikedCombinations(@Body body: LikedCombinationRequest): CombinationsResponse

    @POST("combinations/{combinationId}/like")
    @Headers("Requires-Auth: true")
    suspend fun likeCombination(@Path("combinationId") combinationId: Long): CombinationLikeResponse

    @POST("combinations/{combinationId}/dislike")
    @Headers("Requires-Auth: true")
    suspend fun dislikeCombination(@Path("combinationId") combinationId: Long): CombinationLikeResponse

    @GET("combinations")
    @Headers("Requires-Auth: true")
    suspend fun getCombinations(
        @Query("type") type: String,              // all, beauty, health
        @Query("sort") sort: String,              // popular, latest, lowPrice, highPrice
        @Query("combinationId") combinationId: Long? = null, // 커서 ID
        @Query("limit") limit: Int = 10           // 조회 개수
    ): CombinationsResponse

    @GET("combinations/{id}")
    @Headers("Requires-Auth: true")
    suspend fun getCombinationDetail(
        @Path("id") id: Long
    ): CombinationDetailResponse
}
