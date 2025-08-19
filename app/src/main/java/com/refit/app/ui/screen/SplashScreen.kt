package com.refit.app.ui.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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

@Composable
fun SplashScreen(
    onDecide: (loggedIn: Boolean) -> Unit,
    preview: Boolean = false
) {
    val context = LocalContext.current

    // 프리뷰가 아니면 실제 분기 수행
    if (!preview) {
        LaunchedEffect(Unit) {
            val prefs = context.getSharedPreferences("refit_prefs", Context.MODE_PRIVATE)
            val token = prefs.getString("auth_token", null)
            val isLoggedIn = !token.isNullOrBlank()
            onDecide(isLoggedIn)
        }
    }

    // UI
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