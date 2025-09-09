package com.refit.app.ui.composable.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import java.text.DecimalFormat

@Composable
fun DepositWaitingScreen(
    navController: NavController,
    orderCode: String,
    amount: Long,
    orderName: String?,
    method: String?,
    accountNo: String?,
    bankCode: String?,
    dueDate: String?,        // ISO8601 e.g. 2025-09-13T15:53:34+09:00
    depositor: String?
) {
    val titleStyle = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    val bodyStyle  = TextStyle(fontFamily = Pretendard, fontSize = 14.sp)
    val money = DecimalFormat("#,###").format(amount)
    val scroll = rememberScrollState()

    Scaffold(containerColor = Color.White) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(36.dp))

            // 상단: “입금 대기 중”
            Text("입금 대기 중입니다", style = titleStyle)
            Spacer(Modifier.height(8.dp))
            Text("아래 가상계좌로 입금하면 결제가 완료돼요.", style = bodyStyle.copy(color = Color(0xFF6F737A)))

            Spacer(Modifier.height(24.dp))

            // 요약 카드 (주문요약 + VA 정보)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F8FA))
                    .padding(16.dp)
            ) {
                // 주문명/주문번호
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        val nameLine = orderName?.ifBlank { null } ?: "주문"
                        Text(nameLine, style = bodyStyle.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(Modifier.height(4.dp))
                        Text("주문번호 $orderCode", style = bodyStyle.copy(color = Color(0xFF8C8C8C), fontSize = 12.sp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE8E8EE))
                Spacer(Modifier.height(12.dp))

                // 결제(입금) 금액
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("입금 금액", style = bodyStyle.copy(color = Color(0xFF6F737A)))
                    Text("${money}원", style = bodyStyle.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                }
                if (!method.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("결제수단", style = bodyStyle.copy(color = Color(0xFF6F737A)))
                        Text(methodLabel(method!!), style = bodyStyle)
                    }
                }

                // 가상계좌 블록
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE8E8EE))
                Spacer(Modifier.height(12.dp))

                Text("입금 계좌", style = bodyStyle.copy(fontWeight = FontWeight.SemiBold))
                Spacer(Modifier.height(8.dp))

                VaRow(label = "은행", value = bankName(bankCode))
                Spacer(Modifier.height(6.dp))
                VaRow(label = "계좌번호", value = accountNo, copyable = true)
                Spacer(Modifier.height(6.dp))
                VaRow(label = "예금주", value = depositor?.ifBlank { "토스 가상계좌" } ?: "토스 가상계좌")
                Spacer(Modifier.height(6.dp))
                if (!dueDate.isNullOrBlank()) {
                    VaRow(label = "입금기한", value = humanizeDue(dueDate))
                }
            }

            Spacer(Modifier.height(28.dp))

            // 버튼들
            Button(
                onClick = {
                    // 주문내역으로 이동해 상태 갱신 확인
                    navController.navigate("orders") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MainPurple, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) { Text("주문내역 확인") }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    // 홈
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) { Text("홈으로 가기") }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VaRow(label: String, value: String?, copyable: Boolean = false) {
    val bodyStyle = TextStyle(fontFamily = Pretendard, fontSize = 14.sp)
    val ctx = androidx.compose.ui.platform.LocalContext.current

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = bodyStyle.copy(color = Color(0xFF6F737A)))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value ?: "-", style = bodyStyle.copy(fontWeight = FontWeight.Medium))
            if (copyable && !value.isNullOrBlank()) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { copyToClipboard(ctx, value) }) {
                    Text("복사")
                }
            }
        }
    }
}
// 간단 은행코드 매핑 (필요시 더 확장)
private fun bankName(code: String?): String = when (code) {
    "88", "088" -> "신한은행"
    "90", "090" -> "카카오뱅크"
    "20", "020" -> "우리은행"
    "81", "081" -> "하나은행"
    "11", "011" -> "농협"
    else -> code ?: "-"
}

private fun humanizeDue(iso: String): String = try {
    // 간단 포맷: YYYY-MM-DD HH:mm 까지만
    val odt = java.time.OffsetDateTime.parse(iso)
    odt.toLocalDateTime().toString().replace('T', ' ').substring(0,16)
} catch (_: Throwable) { iso }

private fun copyToClipboard(ctx: android.content.Context, text: String) {
    val cm = ctx.getSystemService(android.content.ClipboardManager::class.java)
    cm.setPrimaryClip(android.content.ClipData.newPlainText("account", text))
    android.widget.Toast.makeText(ctx, "복사되었습니다", android.widget.Toast.LENGTH_SHORT).show()
}