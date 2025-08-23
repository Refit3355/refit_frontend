package com.refit.app.network

import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import org.json.JSONObject

/**
 * 401(Unauthorized) 응답을 받으면:
 * 1) refreshToken으로 /auth/refresh 호출
 * 2) Authorization 헤더에서 새 access 토큰 추출
 * 3) 바디의 data.refreshToken(있으면)도 갱신
 * 4) 원래 요청을 새 access로 재시도
 */
class RefreshAuthenticator(
    private val baseUrlProvider: () -> String
) : Authenticator {

    private val plainClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        // 같은 요청이 연속 2번 이상 401이면 중단 (무한 루프 방지)
        if (responseCount(response) >= 2) return null

        // refreshToken 없으면 재시도 불가
        val refresh = TokenManager.getRefreshToken() ?: return null

        // access가 진짜 만료면 갱신 시도
        val hadAuth = !response.request.header("Authorization").isNullOrBlank()
        if (!hadAuth) return null

        // /auth/refresh 호출
        val url = buildString {
            append(baseUrlProvider())
            append("auth/refresh?refreshToken=")
            append(URLEncoder.encode(refresh, "UTF-8"))
        }

        val refreshReq = Request.Builder()
            .url(url)
            .get()
            .build()

        val refreshResp = try {
            plainClient.newCall(refreshReq).execute()
        } catch (_: Exception) {
            return null
        }

        if (!refreshResp.isSuccessful) {
            // 재발급 실패하면 토큰 삭제(선택) 후 로그인 필요
            return null
        }

        // 새 access 토큰 - Authorization 헤더에서 Bearer <token>
        val newAccess = refreshResp.header("Authorization")
            ?.removePrefix("Bearer")
            ?.trim()

        // 새 refresh - 바디의 data.refreshToken
        val newRefresh = try {
            val bodyStr = refreshResp.body?.string()
            val json = JSONObject(bodyStr ?: "{}")
            val data = json.optJSONObject("data")
            data?.optString("refreshToken")?.takeIf { it.isNotBlank() }
        } catch (_: Exception) { null }

        if (newAccess.isNullOrBlank()) return null

        // 저장
        TokenManager.saveTokens(newAccess, newRefresh)

        // 새 access로 Authorization 교체
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var count = 1
        while (r?.priorResponse != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}