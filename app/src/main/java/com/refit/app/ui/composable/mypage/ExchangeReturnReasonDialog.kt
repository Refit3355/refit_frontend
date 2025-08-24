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
fun ExchangeReturnReasonDialog(
    orderItemId: Long,
    onDismiss: () -> Unit,
    onConfirmExchange: (Long) -> Unit,
    onConfirmReturn: (Long) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var selectedAction by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "단순 변심" to listOf("상품이 마음에 들지 않음"),
        "배송 문제" to listOf(
            "배송된 장소에 박스가 분실됨",
            "다른 주소로 배송됨"
        ),
        "상품 문제" to listOf(
            "상품 내 구성품 부재",
            "상품이 설명과 다름",
            "다른 상품이 배송됨",
            "상품이 파손됨"
        )
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 상단 진행 단계 ProgressBar
                LinearProgressIndicator(
                    progress = { if (step == 1) 0.5f else 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
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
                        text = "교환 / 반품 신청",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    HorizontalDivider()

                    // 사유 선택
                    if (step == 1) {
                        Text(
                            "어떤 문제가 있나요?",
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        categories.forEachIndexed { idx, (category, reasons) ->
                            Text(
                                text = category,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 0.dp)
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
                            if (idx < categories.lastIndex) HorizontalDivider(Modifier.padding(vertical = 4.dp))
                        }
                    }

                    // 해결 방법 선택
                    if (step == 2) {
                        Text(
                            "해결 방법을 선택해주세요",
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        listOf("교환 신청" to "exchange", "반품 신청" to "return").forEach { (label, value) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedAction = value }
                            ) {
                                CustomRadioButton(
                                    selected = selectedAction == value,
                                    onClick = { selectedAction = value }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    label,
                                    fontFamily = Pretendard,
                                    fontSize = 15.sp,
                                    color = if (selectedAction == value) MainPurple else Color.Black
                                )
                            }
                        }
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
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFCCCCCC),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "취소",
                                fontFamily = Pretendard,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                        }

                        // 다음 단계 / 신청 완료 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .background(
                                    if (if (step == 1) selectedReason != null else selectedAction != null)
                                        MainPurple else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    enabled = if (step == 1) selectedReason != null else selectedAction != null
                                ) {
                                    if (step == 1) step = 2
                                    else {
                                        if (selectedAction == "exchange") onConfirmExchange(orderItemId)
                                        if (selectedAction == "return") onConfirmReturn(orderItemId)
                                        onDismiss()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (step == 1) "다음 단계" else "신청 완료",
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
