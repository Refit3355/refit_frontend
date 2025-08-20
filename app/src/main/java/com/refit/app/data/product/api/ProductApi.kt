package com.refit.app.data.product.api

import com.refit.app.data.product.model.ProductDetailResponse
import com.refit.app.data.product.model.ProductListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("sort") sort: String,
        @Query("limit") limit: Int = 20,
        @Query("group") group: String,
        @Query("cursor") cursor: String? = null,
        @Query("categoryId") category: Int? = null
    ): ProductListResponse

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") id: Int
    ): ProductDetailResponse

}