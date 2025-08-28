package com.refit.app.ui.composable.order

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.refit.app.data.payment.api.PaymentApi
import com.refit.app.data.payment.model.ConfirmPaymentRequest
import com.refit.app.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun PayResultHandler(
    navController: NavController,
    paymentKey: String,
    orderId: String,
    amount: Long,
    onSuccessNavigate: (orderPk: Long) -> Unit = { orderPk ->
        // 성공 시 원하는 화면으로 이동 (예: 주문 상세)
        navController.navigate("order/$orderPk") {
            // 결제 플로우 스택 제거
            popUpTo("splash") { inclusive = false }
        }
    },
    onFailNavigate: () -> Unit = {
        navController.navigate("cart") {
            popUpTo("splash") { inclusive = false }
        }
    }
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(paymentKey, orderId, amount) {
        scope.launch {
            runCatching {
                val api = RetrofitInstance.create(PaymentApi::class.java)
                api.confirm(ConfirmPaymentRequest(paymentKey, orderId, amount))
            }.onSuccess { resp ->
                done = true
                val orderPk = resp.runCatching {
                    val f = javaClass.getDeclaredField("orderPk")
                    f.isAccessible = true
                    (f.get(this) as? Long) ?: 0L
                }.getOrDefault(0L)
                if (orderPk > 0) onSuccessNavigate(orderPk) else onSuccessNavigate(0L)
            }.onFailure {
                error = it.message ?: "결제 확인 실패"
                done = true
                onFailNavigate()
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!done && error == null) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text("결제 실패: $error")
        }
    }
}
