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
    private var baseUrlString: String = ""

    private fun isDebuggable(context: Context): Boolean =
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * Application.onCreate() 에서 1회 호출
     * @param baseUrlOverride null 이면 디버그 여부에 따라 자동 선택
     */
    fun init(context: Context, baseUrlOverride: String? = null) {
        if (initialized) return

        val debug = isDebuggable(context)

        // 로깅 인터셉터 - Authorization 헤더 마스킹
        val logging = HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            level = if (debug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }

        baseUrlString = baseUrlOverride ?: if (debug)
            //"http://192.168.5.193:8080/"     // 로컬 + 개발(실제 기기) - http://<PC의 LAN IP>:8080
            //"http://192.168.0.13:8080/"
            "http://10.0.2.2:8080/" // 애뮬레이터
            //"http://127.0.0.1:8080/" // 로컬 + 실제 기기 연결이 안될 때 -> 노션 트러블슈팅에서 읽고 해당 주소로 요청
        else
            "https://api.refit.today/"   // 운영

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(TokenInterceptor())
            .authenticator(RefreshAuthenticator(::baseUrl))
            .addInterceptor(logging)
            .build()

        retrofitInternal = Retrofit.Builder()
            .baseUrl(baseUrlString)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        initialized = true
    }

    fun baseUrl(): String = baseUrlString

    val retrofit: Retrofit
        get() {
            check(initialized) {
                "RetrofitInstance.init(context)를 Application.onCreate()에서 먼저 호출하세요."
            }
            return retrofitInternal
        }

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}