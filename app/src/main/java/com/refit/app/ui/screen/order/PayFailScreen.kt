package com.refit.app.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PayFailScreen(
    navController: NavHostController,
    code: String,
    message: String
) {
    // 진입할 때 실패 사유 로그
    androidx.compose.runtime.LaunchedEffect(code, message) {
        android.util.Log.w("TOSS_FAIL", "code=$code, message=$message")
    }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("결제를 완료할 수 없어요", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text(
                text = message.ifBlank { "결제가 취소되었거나 실패했습니다." },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            AssistChip(
                onClick = { /* noop */ },
                label = { Text("코드: $code") }
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("이전 화면")
                }
                Button(onClick = {
                    // 장바구니로 복귀하거나 주문서로 재시도
                    // 정책에 맞게 수정
                    navController.navigate("cart") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }) {
                    Text("다시 시도")
                }
            }
        }
    }
}
