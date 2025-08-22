package com.refit.app.data.myfit.api

import com.refit.app.data.myfit.model.CreateMemberProductRequest
import com.refit.app.data.myfit.model.MemberProductListResponse
import com.refit.app.data.myfit.model.PurchasedProductResponse
import com.refit.app.data.myfit.model.UpdateMemberProductRequest
import retrofit2.Response
import retrofit2.http.*

interface MyfitApi {
    @GET("member-products")
    suspend fun getMemberProducts(
        @Query("type") type: String,      // beauty | health
        @Query("status") status: String   // using | completed | all
    ): MemberProductListResponse

    @PATCH("member-products/{memberProductId}/status")
    suspend fun updateMemberProductStatus(
        @Path("memberProductId") id: Long,
        @Query("status") status: String   // using | completed
    ): Response<Unit>

    @DELETE("member-products/{memberProductId}")
    suspend fun deleteMemberProduct(
        @Path("memberProductId") id: Long
    ): Response<Unit>

    @POST("member-products/custom")
    suspend fun createCustomMemberProduct(
        @Body body: CreateMemberProductRequest
    ): Response<Unit>

    @PATCH("member-products/{memberProductId}")
    suspend fun updateMemberProduct(
        @Path("memberProductId") id: Long,
        @Body body: UpdateMemberProductRequest
    ): Response<Unit>

    @GET("/orders/unregistered-products")
    suspend fun getPurchasedProducts(@Query("type") type: String): PurchasedProductResponse

    @POST("member-products/order-item/{orderItemId}")
    suspend fun createFromOrderItem(
        @Path("orderItemId") id: Long
    ): Response<Unit>
}
