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
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApi {
    @GET("/cart/count")
    @Headers("Requires-Auth: true")
    suspend fun getCartCount(): CartCountResponse

    @GET("/cart")
    @Headers("Requires-Auth: true")
    suspend fun getCart(): CartListResponse

    @POST("/cart/items")
    @Headers("Requires-Auth: true")
    suspend fun addCartItem(@Body body: CartAddRequest): Unit

    @POST("/cart/items/bulk")
    @Headers("Requires-Auth: true")
    suspend fun addCartItemsBulk(@Body body: CartAddBulkRequest): Unit

    @DELETE("/cart/items/{cartId}")
    @Headers("Requires-Auth: true")
    suspend fun deleteCartItem(@Path("cartId") cartId: Long): Unit

    @HTTP(method = "DELETE", path = "/cart/items/bulk", hasBody = true)
    @Headers("Requires-Auth: true")
    suspend fun deleteCartItemsBulk(@Body body: CartDeleteBulkRequest): Unit

    @POST("/cart/items/{itemId}")
    @Headers("Requires-Auth: true")
    suspend fun updateCartQuantity(
        @Path("itemId") cartId: Long,
        @Body body: CartQuantityUpdateRequest
    ): Unit
}