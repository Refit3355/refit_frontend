package com.refit.app.data.me.api

import com.refit.app.data.local.combination.model.CombinationsResponse
import com.refit.app.data.me.model.LikeRequest
import com.refit.app.data.me.model.LikeResponse
import com.refit.app.data.me.model.OrdersResponse
import com.refit.app.data.me.model.ProfileImageResponse
import com.refit.app.data.me.model.UpdateOrderStatusResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    @Multipart
    @POST("/me/profile/image/update")
    @Headers("Requires-Auth: true")
    suspend fun updateProfileImage(
        @Part profileImage: MultipartBody.Part
    ): ProfileImageResponse

    @POST("/orders/{orderItemId}/exchange")
    @Headers("Requires-Auth: true")
    suspend fun requestExchange(@Path("orderItemId") orderItemId: Long): UpdateOrderStatusResponse

    @POST("/orders/{orderItemId}/return")
    @Headers("Requires-Auth: true")
    suspend fun requestReturn(@Path("orderItemId") orderItemId: Long): UpdateOrderStatusResponse

}