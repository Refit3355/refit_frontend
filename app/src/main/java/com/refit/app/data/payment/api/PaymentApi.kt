package com.refit.app.data.payment.api

import com.refit.app.data.payment.model.ConfirmPaymentRequest
import com.refit.app.data.payment.model.ConfirmPaymentResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentApi {
    @POST("payments/confirm")
    @Headers("Requires-Auth: true")
    suspend fun confirm(@Body req: ConfirmPaymentRequest): ConfirmPaymentResponse
}