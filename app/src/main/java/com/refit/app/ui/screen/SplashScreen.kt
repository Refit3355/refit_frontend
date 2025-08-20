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
import com.refit.app.network.TokenManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onDecide: (loggedIn: Boolean) -> Unit,
    preview: Boolean = false,
    minDurationMs: Long = 1000L  // 최소 노출 시간
) {
    // val context = LocalContext.current

    LaunchedEffect(preview) {
        if (preview) return@LaunchedEffect

        val start = System.currentTimeMillis()

        // 로그인 여부 판단 (현재 사용중인 SharedPreferences 로직 유지)
        val access = TokenManager.getAccessToken()
        val isLoggedIn = !access.isNullOrBlank()

        // 최소 노출 시간 보장
        val elapsed = System.currentTimeMillis() - start
        if (elapsed < minDurationMs) {
            delay(minDurationMs - elapsed)
        }

        onDecide(isLoggedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_login_logo),
                contentDescription = null,
                modifier = Modifier.size(250.dp)
            )
        }
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