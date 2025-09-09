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
import coil.compose.AsyncImage
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import java.text.DecimalFormat

@Composable
fun OrderCompleteScreen(
    navController: NavController,
    orderCode: String,
    amount: Long,
    orderName: String?,
    method: String?,
    thumb: String?,
    itemCount: Int?,
    items: List<CompleteItemNav> = emptyList()
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

            // 상단 축하/완료 영역
            Text("주문이 완료되었습니다", style = titleStyle)
            Spacer(Modifier.height(8.dp))
            Text("주문 내역은 마이 > 주문내역에서 확인할 수 있어요.", style = bodyStyle.copy(color = Color(0xFF6F737A)))

            Spacer(Modifier.height(24.dp))

            // 요약 카드 (결제 정보 + 주문 상품을 '같은 카드'에 배치)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F8FA))
                    .padding(16.dp)
            ) {
                // 상품명/주문번호
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        val nameLine = when {
                            !orderName.isNullOrBlank() -> orderName
                            (itemCount ?: -1) > 0      -> "상품 ${itemCount}개"
                            else                       -> "상품"
                        }
                        Text(nameLine ?: "상품", style = bodyStyle.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(Modifier.height(4.dp))
                        Text("주문번호 $orderCode", style = bodyStyle.copy(color = Color(0xFF8C8C8C), fontSize = 12.sp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE8E8EE))
                Spacer(Modifier.height(12.dp))

                // 결제 정보
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("결제금액", style = bodyStyle.copy(color = Color(0xFF6F737A)))
                    Text("${money}원", style = bodyStyle.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                }
                if (!method.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("결제수단", style = bodyStyle.copy(color = Color(0xFF6F737A)))
                        Text(methodLabel(method), style = bodyStyle)
                    }
                }

                // 주문 상품 (카드 내부, 결제 정보 아래)
                if (items.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE8E8EE))
                    Spacer(Modifier.height(12.dp))

                    Text("주문 상품", style = bodyStyle.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(Modifier.height(8.dp))

                    items.forEach { it ->
                        OrderItemMiniRow(
                            item = CompleteItemUi(
                                productId     = it.productId,
                                brand         = it.brand,           // 서버 필드명은 brandName, 네비 DTO가 brand로 받음
                                productName   = it.productName,
                                price         = it.price,
                                originalPrice = it.originalPrice,
                                quantity      = it.quantity,
                                thumbnailUrl  = it.thumbnailUrl
                            ),
                            onClickProduct = { pid ->
                                navController.navigate("product/$pid")
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // 홈으로 가기
            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MainPurple, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("홈으로 가기")
            }
        }
    }
}

fun methodLabel(code: String): String = when (code) {
    "CARD" -> "신용카드"
    "VIRTUAL_ACCOUNT" -> "가상계좌"
    "TRANSFER" -> "계좌이체"
    "MOBILE_PHONE" -> "휴대폰결제"
    else -> code
}
