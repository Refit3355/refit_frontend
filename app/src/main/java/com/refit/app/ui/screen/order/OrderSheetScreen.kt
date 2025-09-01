package com.refit.app.ui.screen.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.refit.app.data.order.api.OrderApi
import com.refit.app.data.order.model.*
import com.refit.app.data.order.modelAndView.OrderSheetUi
import com.refit.app.data.order.modelAndView.OrderSheetViewModel
import com.refit.app.data.payment.api.PaymentApi
import com.refit.app.network.RetrofitInstance
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import java.text.DecimalFormat
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@Composable
fun OrderSheetScreen(
    navController: NavController,
    draftReq: DraftOrderRequest,
    successUrl: String = "refitapp://pay/success",
    failUrl: String = "refitapp://pay/fail"
) {
    val vm: OrderSheetViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val orderApi = RetrofitInstance.create(OrderApi::class.java)
                val paymentApi = RetrofitInstance.create(PaymentApi::class.java)
                OrderSheetViewModel(orderApi, paymentApi)
            }
        }
    )
    val ui by vm.ui.collectAsState()

    LaunchedEffect(draftReq) { vm.loadDraft(draftReq) }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column {
                Divider(color = Color(0xFFEDEDED), thickness = 1.dp)
                Surface(color = Color.White, tonalElevation = 0.dp) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val enabled = ui.draft != null && ui.consentTerms
                        val amountText = ui.draft?.totalAmount?.let(::won) ?: "0"
                        Button(
                            onClick = {
                                val d = ui.draft ?: return@Button
                                navController.navigate(
                                    "tossPay?orderId=${d.orderCode}" +
                                            "&orderName=${encode(d.orderSummary)}" +
                                            "&amount=${d.totalAmount}" +
                                            "&method=${ui.selectedMethod}" +
                                            "&successUrl=${encode(successUrl)}" +
                                            "&failUrl=${encode(failUrl)}"
                                )
                            },
                            enabled = enabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(13.21.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainPurple,        // 활성화시 색상
                                disabledContainerColor = Color(0xFFE0E0E6), // 비활성화 색상
                                contentColor = Color.White          // 텍스트 흰색
                            )
                        ) {
                            Text("${amountText}원 결제하기")
                        }
                    }
                }
            }
        }
    ) { inner ->
        when {
            ui.loading -> Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            ui.error != null -> Text(
                modifier = Modifier.padding(inner).padding(20.dp),
                text = "오류: ${ui.error}"
            )

            else -> {
                val draft = ui.draft!!

                val amountSummary = AmountSummary(
                    goodsAmount = draft.goodsAmount,
                    discount = draft.discount,
                    deliveryFee = draft.deliveryFee,
                    finalPay = draft.totalAmount
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(inner)
                        .background(Color.White),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // 1) 배송 정보
                    item { SectionTitle("배송 정보") }
                    item { SectionShipping(draft.shipping) }
                    item { SectionSeparator() }

                    // 2) 주문 상품
                    item { SectionTitle("주문상품") }
                    item { SectionItems(draft.items) }
                    item { SectionSeparator() }

                    // 3) 결제 수단
                    item { SectionTitle("결제수단") }
                    item { SectionPayMethod(ui.selectedMethod, onSelect = vm::selectMethod) }
                    item { SectionSeparator() }

                    // 4) 결제 금액
                    item { SectionTitle("결제금액") }
                    item { SectionAmountBlock(amountSummary) }
                    item { SectionSeparator() }

                    // 5) 약관 동의
                    item { SectionTerms(ui.consentTerms, onToggle = vm::toggleTerms) }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

/* ================= 스타일 & 유틸 ================= */

private val TitleTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)
private val BodyTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontSize = 14.sp
)

private val moneyFormatter = DecimalFormat("#,###")
private fun won(n: Long) = moneyFormatter.format(n)

/* ================= 공통 섹션 요소 ================= */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = TitleTextStyle,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

/** 각 영역을 이미지처럼 두꺼운 회색 블럭으로 구분 */
@Composable
private fun SectionSeparator() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFF5F5F7))
            .padding(vertical = 20.dp)
    )
}

/* ================= 섹션: 배송 정보 ================= */

@Composable
private fun SectionShipping(info: ShippingInfo) {
    // 얇은 연회색 테두리 + 둥근 모서리 + 내부 흰색 카드
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                BorderStroke(1.dp, Color(0xFFE7E7EA)),
                RoundedCornerShape(12.dp)
            )
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("${info.receiverName} · ${info.phone}", style = BodyTextStyle)
        Spacer(Modifier.height(6.dp))
        Text("${info.roadAddress} ${info.detailAddress} [${info.zipcode}]", style = BodyTextStyle)
        info.memo?.let {
            Spacer(Modifier.height(6.dp))
            Text("배송메모 : $it", style = BodyTextStyle)
        }
    }
}

/* ================= 섹션: 주문 상품 ================= */

@Composable
private fun SectionItems(items: List<OrderItemSummary>) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        items.forEach { it ->
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                AsyncImage(
                    model = it.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(it.productName, maxLines = 2, style = BodyTextStyle)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        it.brandName,
                        style = BodyTextStyle.copy(color = Color(0xFF8C8C8C), fontSize = 12.sp)
                    )

                    Spacer(Modifier.height(6.dp))

                    // 할인가 / 원가 / 수량
                    val salePrice = it.price
                    val originalPrice = it.originalPrice

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${won(salePrice)} 원",
                            style = BodyTextStyle.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        )
                        if (originalPrice > salePrice) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${won(originalPrice)} 원",
                                style = BodyTextStyle.copy(
                                    color = Color(0xFF9FA4AB),
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "· ${it.quantity}개",
                            style = BodyTextStyle.copy(color = Color(0xFF6F737A), fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}

/* ================= 섹션: 결제 수단 (버튼 UI) ================= */

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionPayMethod(selected: String, onSelect: (String) -> Unit) {
    val options = listOf(
        "CARD" to "신용카드",
        "VIRTUAL_ACCOUNT" to "가상계좌"
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = (value == selected)
            val borderColor = if (isSelected) MainPurple else Color(0xFFE0E0E6)
            val bg = if (isSelected) MainPurple.copy(alpha = 0.08f) else Color.White
            OutlinedButton(
                onClick = { onSelect(value) },
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = bg),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(label, style = BodyTextStyle.copy(fontWeight = FontWeight.Medium))
            }
        }
    }
}

/* ================= 섹션: 결제 금액 ================= */

private data class AmountSummary(
    val goodsAmount: Long,   // 할인가 총액(= 주문금액)
    val discount: Long,      // 할인 총액
    val deliveryFee: Long,   // 서버 제공
    val finalPay: Long       // 최종 결제금액
)

@Composable
private fun SectionAmountBlock(summary: AmountSummary) {
    val productTotal = summary.goodsAmount + summary.discount // 정가 총액

    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // 상품금액(정가 총액)
        AmountRow("상품금액(정가)", "${won(productTotal)} 원")
        // 할인금액
        AmountRow("할인금액", "-${won(summary.discount)} 원")
        // 주문금액(할인가 총액)
        AmountRow("주문금액(할인가)", "${won(summary.goodsAmount)} 원")

        // 배송비는 그대로 표기
        AmountRow("배송비", "${won(summary.deliveryFee)} 원")

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            color = Color(0xFFEDEDED),
            thickness = 1.dp
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("최종 결제금액", style = BodyTextStyle.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            Text(
                "${won(summary.finalPay)} 원",
                style = BodyTextStyle.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
        }
    }
}

@Composable
private fun AmountRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = BodyTextStyle.copy(color = Color(0xFF6F737A)))
        Text(value, style = BodyTextStyle)
    }
}

/* ================= 섹션: 약관 동의 ================= */

@Composable
private fun SectionTerms(checked: Boolean, onToggle: (Boolean) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("전자금융거래 이용약관 및 결제대행 동의 (필수)", style = BodyTextStyle)
            }
            Checkbox(
                checked = checked,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(checkedColor = MainPurple)
            )
        }
    }
}

/* ================= 기타 ================= */

private fun encode(s: String) = java.net.URLEncoder.encode(s, "utf-8")
