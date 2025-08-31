package com.refit.app.ui.composable.mypage

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
    unitPrice: Int,
    maxQty: Int,           // 최대 취소 가능 수량 (UI 상에선 item.quantity 사용)
    onDismiss: () -> Unit,
    onConfirmCancel: (orderItemId: Long, reason: String, count: Int) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var selectedCount by remember { mutableStateOf(1) }

    val reasons = listOf("상품이 마음에 들지 않음", "더 저렴한 상품을 발견함", "잘못된 상품을 주문함")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
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
                    Text("주문 취소", fontFamily = Pretendard, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    HorizontalDivider()

                    if (step == 1) {
                        Text("취소 사유를 선택해주세요", fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        reasons.forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedReason = reason }
                            ) {
                                CustomRadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason })
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    reason,
                                    fontFamily = Pretendard,
                                    fontSize = 14.sp,
                                    color = if (selectedReason == reason) MainPurple else Color.Black
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        // 수량 선택
                        Text("취소 수량", fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MyOrderActionButton(text = "－", modifier = Modifier.width(48.dp).height(32.dp)) {
                                if (selectedCount > 1) selectedCount--
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$selectedCount 개",
                                    fontFamily = Pretendard,
                                    fontSize = 16.sp
                                )
                            }
                            MyOrderActionButton(text = "＋", modifier = Modifier.width(48.dp).height(32.dp)) {
                                if (selectedCount < maxQty) selectedCount++
                            }
                        }

                        // 미리보기(환불 금액)
                        Text(
                            text = "환불 예정 금액: ${unitPrice * selectedCount}원",
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MainPurple
                        )
                    }

                    if (step == 2) {
                        Text(
                            "결제 금액은 영업일 기준 1일 이내에 자동 환불 처리됩니다.",
                            fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MyOrderActionButton(
                            text = "닫기",
                            modifier = Modifier.weight(1f).height(42.dp),
                            onClick = onDismiss
                        )
                        MyOrderActionButton(
                            text = if (step == 1) "다음 단계" else "환불 신청",
                            modifier = Modifier.weight(1f).height(42.dp),
                            onClick = {
                                if (step == 1) {
                                    if (selectedReason != null) step = 2
                                } else {
                                    onConfirmCancel(orderItemId, selectedReason ?: "기타", selectedCount)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}