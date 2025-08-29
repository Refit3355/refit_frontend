package com.refit.app.data.payment.api

import com.refit.app.data.payment.model.ConfirmPaymentRequest
import com.refit.app.data.payment.model.ConfirmPaymentResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import kotlinx.serialization.json.JsonObject

interface PaymentApi {
    @POST("payments/confirm")
    @Headers("Requires-Auth: true")
    suspend fun confirm(@Body req: ConfirmPaymentRequest): ConfirmPaymentResponse

    @POST("payments/confirm")
    @Headers("Requires-Auth: true")
    suspend fun confirmRaw(@Body req: ConfirmPaymentRequest): okhttp3.ResponseBody
}