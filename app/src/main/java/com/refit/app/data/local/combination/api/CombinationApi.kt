package com.refit.app.data.local.combination.api

import com.refit.app.data.local.combination.model.CombinationLikeResponse
import com.refit.app.data.local.combination.model.LikedCombinationRequest
import com.refit.app.data.local.combination.model.CombinationsResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

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
}
