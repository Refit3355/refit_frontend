package com.refit.app.network

import retrofit2.Retrofit
import android.content.Context
import android.content.pm.ApplicationInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    @Volatile private var initialized = false
    private lateinit var retrofitInternal: Retrofit

    private fun isDebuggable(context: Context): Boolean =
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * Application.onCreate() 에서 1회 호출
     * @param baseUrlOverride null 이면 디버그 여부에 따라 자동 선택
     */
    fun init(context: Context, baseUrlOverride: String? = null) {
        if (initialized) return

        val debug = isDebuggable(context)
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }

        val baseUrl = baseUrlOverride ?: if (debug)
            "http://172.30.1.60:8080/"     // 개발(실제 기기)
        else
            "https://server-address/"   // 운영

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(TokenInterceptor())
            .addInterceptor(logging)
            .build()

        retrofitInternal = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        initialized = true
    }

    val retrofit: Retrofit
        get() {
            check(initialized) {
                "RetrofitInstance.init(context)를 Application.onCreate()에서 먼저 호출하세요."
            }
            return retrofitInternal
        }

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}