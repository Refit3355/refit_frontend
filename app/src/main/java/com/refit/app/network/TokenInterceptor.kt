package com.refit.app.network

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val access = TokenManager.getAccessToken()
        val req = chain.request().newBuilder().apply {
            if (!access.isNullOrBlank()) addHeader("Authorization", "Bearer $access")
        }.build()
        return chain.proceed(req)
    }
}