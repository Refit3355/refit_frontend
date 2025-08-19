package com.refit.app.data.product.api

import com.refit.app.data.product.model.ProductDetailResponse
import com.refit.app.data.product.model.ProductDto
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

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") q: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("sort") sort: String? = null
    ): ProductListResponse

    @GET("products/popular")
    suspend fun getPopularProducts(
        @Query("limit") limit: Int = 10
    ): List<ProductDto>
}