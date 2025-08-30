package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CancelOrderReasonDialog(
    orderItemId: Long,
    onDismiss: () -> Unit,
    onConfirmCancel: (Long) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedReason by remember { mutableStateOf<String?>(null) }

    val reasons = listOf(
        "상품이 마음에 들지 않음",
        "더 저렴한 상품을 발견함",
        "잘못된 상품을 주문함"
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(3.dp, Color.White, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 상단 단계 표시
                LinearProgressIndicator(
                    progress = { if (step == 1) 0.5f else 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clipToBounds(),
                    color = MainPurple,
                    trackColor = Color(0xFFEAEAEA),
                    strokeCap = StrokeCap.Butt
                )

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 타이틀
                    Text(
                        text = "주문 취소",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    HorizontalDivider()

                    // 취소 사유 선택
                    if (step == 1) {
                        Text(
                            "취소 사유를 선택해주세요",
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        reasons.forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedReason = reason }
                            ) {
                                CustomRadioButton(
                                    selected = selectedReason == reason,
                                    onClick = { selectedReason = reason }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    reason,
                                    fontFamily = Pretendard,
                                    fontSize = 14.sp,
                                    color = if (selectedReason == reason) MainPurple else Color.Black
                                )
                            }
                        }
                    }

                    // 결제 환불 버튼
                    if (step == 2) {
                        Text(
                            "결제 금액은 영업일 기준 1일 이내에 자동 환불 처리됩니다.",
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // 하단 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 취소 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(8.dp))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "닫기",
                                fontFamily = Pretendard,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                        }

                        // 다음 or 결제 환불 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .background(
                                    if (if (step == 1) selectedReason != null else true)
                                        MainPurple else Color(0xFFE0E0E0),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    enabled = if (step == 1) selectedReason != null else true
                                ) {
                                    if (step == 1) step = 2
                                    else {
                                        onConfirmCancel(orderItemId)
                                        onDismiss()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (step == 1) "다음 단계" else "환불 신청",
                                fontFamily = Pretendard,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
