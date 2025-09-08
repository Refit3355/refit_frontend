package com.refit.app.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.network.JwtUtils
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

@Composable
fun SplashScreen(
    onDecide: (loggedIn: Boolean) -> Unit,
    preview: Boolean = false,
    minDurationMs: Long = 800L,  // 최소 노출 시간
) {
    LaunchedEffect(preview) {
        if (preview) return@LaunchedEffect

        val startedAt = System.currentTimeMillis()

        // 로그인 여부 판단 (현재 사용중인 SharedPreferences 로직 유지)
        val access = TokenManager.getAccessToken()
        var canEnter = JwtUtils.isAccessUsable(
            token = access,
            expectedIssuer = null,   // 서버 issuer를 아는 경우 문자열 넣으면 더 안전
            leewaySec = 45
        )
        if (!canEnter) {
            canEnter = trySilentRefresh()
        }

        val elapsed = System.currentTimeMillis() - startedAt
        if (elapsed < minDurationMs) delay(minDurationMs - elapsed)

        // 4) 화면 이동
        onDecide(canEnter)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier.size(270.dp)
            )
        }
    }
}

/**
 * access 만료 시, refreshToken으로 재발급 시도 (GET 쿼리 방식 유지)
 * 성공 시 TokenManager에 저장하고 true 반환
 */
private suspend fun trySilentRefresh(): Boolean = withContext(Dispatchers.IO) {
    val refresh = TokenManager.getRefreshToken() ?: return@withContext false

    // Authenticator/Interceptor 없는 순수 클라이언트
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val url = buildString {
        append(RetrofitInstance.baseUrl())
        append("auth/refresh?refreshToken=")
        append(URLEncoder.encode(refresh, "UTF-8"))
    }

    val req = Request.Builder().url(url).get().build()

    return@withContext try {
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use false

            // 1) 헤더에서 Bearer access 추출 시도
            val headerAccess = resp.header("Authorization")
                ?.removePrefix("Bearer")
                ?.trim()

            // 2) 바디에서 access/refresh 추출 (data.accessToken, data.refreshToken)
            val bodyStr = resp.body?.string().orEmpty()
            val data = bodyStr
                .takeIf { it.isNotBlank() }
                ?.let { JSONObject(it).optJSONObject("data") }

            val bodyAccess = data?.optString("accessToken")?.takeIf { it.isNotBlank() }
            val bodyRefresh = data?.optString("refreshToken")?.takeIf { it.isNotBlank() }

            val newAccess = headerAccess ?: bodyAccess
            if (newAccess.isNullOrBlank()) return@use false

            TokenManager.saveTokens(newAccess, bodyRefresh) // bodyRefresh 없으면 기존 refresh 유지
            true
        }
    } catch (_: Exception) {
        false
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewSplashScreen() {
    MaterialTheme {
        SplashScreen(
            onDecide = {},
            preview = true
        )
    }
}