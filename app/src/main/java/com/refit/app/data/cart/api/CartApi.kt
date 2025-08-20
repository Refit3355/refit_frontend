package com.refit.app.data.cart.api

import com.refit.app.data.cart.model.CartAddBulkRequest
import com.refit.app.data.cart.model.CartAddRequest
import com.refit.app.data.cart.model.CartCountResponse
import com.refit.app.data.cart.model.CartDeleteBulkRequest
import com.refit.app.data.cart.model.CartListResponse
import com.refit.app.data.cart.model.CartQuantityUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApi {
    @GET("/cart/count")
    suspend fun getCartCount(): CartCountResponse

    @GET("/cart")
    suspend fun getCart(): CartListResponse

    @POST("/cart/items")
    suspend fun addCartItem(@Body body: CartAddRequest): Unit

    @POST("/cart/items/bulk")
    suspend fun addCartItemsBulk(@Body body: CartAddBulkRequest): Unit

    @DELETE("/cart/items/{cartId}")
    suspend fun deleteCartItem(@Path("cartId") cartId: Long): Unit

    @HTTP(method = "DELETE", path = "/cart/items/bulk", hasBody = true)
    suspend fun deleteCartItemsBulk(@Body body: CartDeleteBulkRequest): Unit

    @POST("/cart/items/{itemId}")
    suspend fun updateCartQuantity(
        @Path("itemId") cartId: Long,
        @Body body: CartQuantityUpdateRequest
    ): Unit
}