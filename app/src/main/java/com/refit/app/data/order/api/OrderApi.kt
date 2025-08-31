package com.refit.app.data.order.api

import com.refit.app.data.order.model.DraftOrderRequest
import com.refit.app.data.order.model.DraftOrderResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OrderApi {
    @POST("orders/draft")
    @Headers("Requires-Auth: true")
    suspend fun createDraft(@Body req: DraftOrderRequest): DraftOrderResponse
}
