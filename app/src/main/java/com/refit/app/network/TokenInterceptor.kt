package com.refit.app.network

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        // 이 호출이 토큰을 필요로 하는지 여부 확인
        val requiresAuth = req.header("Requires-Auth")?.equals("true", ignoreCase = true) == true

        val builder = req.newBuilder()
            .removeHeader("Requires-Auth")

        // 토큰이 필요한 경우에만 Authorization 추가
        if (requiresAuth) {
            val access = TokenManager.getAccessToken()
            if (!access.isNullOrBlank() && req.header("Authorization").isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $access")
            }
        }
        return chain.proceed(builder.build())
    }
}