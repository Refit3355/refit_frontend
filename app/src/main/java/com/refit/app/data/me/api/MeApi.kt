package com.refit.app.data.me.api

import com.refit.app.data.me.model.LikeRequest
import com.refit.app.data.me.model.LikeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MeApi {
    @POST("products/like")
    suspend fun getLikedProducts(@Body body: LikeRequest): LikeResponse
}