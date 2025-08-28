package com.refit.app.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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


@Composable
fun OrderSheetScreen(
    navController: NavController,
    draftReq: DraftOrderRequest,           // 네비게이션 파라미터로 전달
    clientKey: String,
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

    LaunchedEffect(draftReq) {
        vm.loadDraft(draftReq)
    }

    Scaffold(
        bottomBar = {
            BottomPayBar(
                ui = ui,
                onClickPay = {
                    val d = ui.draft ?: return@BottomPayBar
                    // 토스 결제창 화면으로 이동
                    navController.navigate(
                        "tossPay?orderId=${d.orderCode}" +
                                "&orderName=${encode(d.orderSummary)}" +
                                "&amount=${d.totalAmount}" +
                                "&method=${ui.selectedMethod}" +
                                "&clientKey=$clientKey" +
                                "&successUrl=${encode(successUrl)}" +
                                "&failUrl=${encode(failUrl)}"
                    )
                }
            )
        }
    ) { inner ->
        when {
            ui.loading -> Box(Modifier.padding(inner).fillMaxSize()) { CircularProgressIndicator() }
            ui.error != null -> Text(
                modifier = Modifier.padding(inner).padding(20.dp),
                text = "오류: ${ui.error}"
            )
            else -> {
                val draft = ui.draft!!
                LazyColumn(
                    modifier = Modifier.padding(inner),
                    contentPadding = PaddingValues(bottom = 100.dp) // 하단 버튼 공간
                ) {
                    item { SectionShipping(draft.shipping) }
                    item { SectionItems(draft.items) }
                    item { SectionPayMethod(ui.selectedMethod, onSelect = vm::selectMethod) }
                    item { SectionAmount(draft.totalAmount) }
                    item { SectionTerms(ui.consentTerms, onToggle = vm::toggleTerms) }
                }
            }
        }
    }
}

@Composable
private fun SectionShipping(info: ShippingInfo) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("배송지 정보", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("${info.receiverName} · ${info.phone}")
        Text("${info.roadAddress} ${info.detailAddress} (${info.zipcode})")
        info.memo?.let { Text("요청사항: $it") }
    }
}

@Composable
private fun SectionItems(items: List<OrderItemSummary>) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("주문 상품", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        items.forEach { it ->
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                AsyncImage(
                    model = it.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(it.productName, maxLines = 1)
                    Text(it.brandName, style = MaterialTheme.typography.bodySmall)
                    Text("${it.price}원 × ${it.quantity}")
                }
            }
        }
    }
}

@Composable
private fun SectionPayMethod(selected: String, onSelect: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("결제 수단", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        val options = listOf("CARD","TRANSFER","VIRTUAL_ACCOUNT","MOBILE_PHONE")
        options.forEach { m ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(m)
                RadioButton(selected = (m==selected), onClick = { onSelect(m) })
            }
        }
        Text("※ 실제 결제창에서 지원 가능한 수단만 노출/선택됩니다.")
    }
}

@Composable
private fun SectionAmount(totalAmount: Long) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("결제 금액", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("총 결제금액: ${totalAmount}원", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun SectionTerms(checked: Boolean, onToggle: (Boolean) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("약관 동의", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("전자금융거래 이용약관 및 결제대행 동의")
            Checkbox(checked = checked, onCheckedChange = onToggle)
        }
    }
}

@Composable
private fun BottomPayBar(ui: OrderSheetUi, onClickPay: () -> Unit) {
    Surface(tonalElevation = 6.dp) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val enabled = ui.draft != null && ui.consentTerms
            Button(
                onClick = onClickPay,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("결제") }
        }
    }
}

private fun encode(s: String) = java.net.URLEncoder.encode(s, "utf-8")
