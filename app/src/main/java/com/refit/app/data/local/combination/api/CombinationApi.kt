package com.refit.app.data.local.combination.api

import com.refit.app.data.local.combination.model.LikedCombinationRequest
import com.refit.app.data.local.combination.model.CombinationsResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CombinationApi {
    @POST("combinations/like")
    @Headers("Requires-Auth: true")
    suspend fun getLikedCombinations(@Body body: LikedCombinationRequest): CombinationsResponse
}
