package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.data.myfit.viewmodel.ErrorKind

@Composable
fun MyfitErrorPlaceholder(kind: ErrorKind, onRetry: () -> Unit) {
    val (illust, title, desc) = when (kind) {
        ErrorKind.Server ->
            Triple(R.drawable.illu_server_error, "앗! 잠시 오류가 발생했어요", "잠시 후 다시 시도해 주세요.")
        ErrorKind.Network ->
            Triple(R.drawable.illu_network_error, "네트워크에 연결할 수 없어요", "Wi-Fi/데이터 연결을 확인해 주세요.")
        ErrorKind.Timeout ->
            Triple(R.drawable.illu_network_error, "응답이 지연되고 있어요", "잠시 후 다시 시도해 주세요.")
        ErrorKind.Client, ErrorKind.Unknown ->
            Triple(R.drawable.illu_server_error, "요청을 처리할 수 없어요", "잠시 후 다시 시도해 주세요.")
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(illust), contentDescription = null)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(desc, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("다시 시도") }
    }
}
