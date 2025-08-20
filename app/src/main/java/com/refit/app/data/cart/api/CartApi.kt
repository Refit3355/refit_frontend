package com.refit.app.data.cart.api

import com.refit.app.data.cart.model.CartCountResponse
import retrofit2.http.GET

interface CartApi {
    @GET("/cart/count")
    suspend fun getCartCount(): CartCountResponse
}