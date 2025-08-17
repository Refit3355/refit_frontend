package com.refit.app.network

import okhttp3.Interceptor

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.request().newBuilder().apply {
            TokenManager.getToken()?.takeIf { it.isNotBlank() }?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()
    )
}