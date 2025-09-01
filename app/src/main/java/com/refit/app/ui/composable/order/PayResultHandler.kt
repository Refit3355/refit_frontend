package com.refit.app.ui.composable.order

import androidx.compose.foundation.Image
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
import kotlinx.serialization.json.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple

@Composable
fun PayResultHandler(
    navController: NavController,
    paymentKey: String,
    orderId: String,
    amount: Long,
    onSuccessNavigate: (orderPk: Long) -> Unit = { orderPk ->
        navController.navigate("orders") {
            popUpTo("splash") { inclusive = false }
        }
    },
    onFailNavigate: () -> Unit = {} // 자동 이동 금지
) {
    val scope = rememberCoroutineScope()

    // 실패 감지용 플래그
    var stockFail by remember { mutableStateOf(false) }
    var netErr by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    // 실패 바디인지(200이라도) 탐지: code가 있거나 error.code가 있으면 실패로 간주
    fun isFail(json: JsonObject): Boolean {
        fun JsonElement?.asText(): String? =
            (this as? JsonPrimitive)?.let { if (it.isString) it.contentOrNull else it.longOrNull?.toString() }
        val code = json["code"].asText()
        val errCode = (json["error"] as? JsonObject)?.get("code").asText()
        return !code.isNullOrBlank() || !errCode.isNullOrBlank()
    }

    LaunchedEffect(paymentKey, orderId, amount) {
        scope.launch {
            runCatching {
                val api = RetrofitInstance.create(PaymentApi::class.java)
                val body = api.confirmRaw(ConfirmPaymentRequest(paymentKey, orderId, amount))
                val jsonString = body.string()
                val json = Json.parseToJsonElement(jsonString).jsonObject

                if (isFail(json)) {
                    stockFail = true
                    loading = false
                    return@launch
                }

                val orderPk = json["orderPk"]?.jsonPrimitive?.longOrNull ?: 0L
                loading = false
                onSuccessNavigate(orderPk)
            }.onFailure { t ->
                netErr = t.message ?: "결제 확인 실패"
                loading = false
            }
        }
    }

    // 로딩
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (loading) CircularProgressIndicator()
    }

    // ==== 재고 부족 모달 ====
    if (stockFail) {
        AlertDialog(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_exclamation_fill),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            },
            onDismissRequest = { /* 바깥터치로 닫히지 않게 */ },
            containerColor = Color.White,
            title = {
                Text(
                    text = "재고가 부족하여 결제를 진행할 수 없습니다.",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )
            },
            text = null,
            confirmButton = {
                Button(
                    onClick = {
                        stockFail = false
                        navController.navigate("cart") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainPurple)
                ) {
                    Text("장바구니로", color = Color.White)
                }
            },
            dismissButton = {}
        )
    }

    // 네트워크/파싱 오류도 자동 이동 없이 모달로만 처리
    if (netErr != null) {
        AlertDialog(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_exclamation_fill),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            },
            onDismissRequest = { /* 바깥터치 닫기 금지 */ },
            containerColor = Color.White,
            title = {
                Text(
                    text = "네트워크 문제로 결제를 확인할 수 없습니다.",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )
            },
            text = null,
            confirmButton = {
                Button(
                    onClick = {
                        netErr = null
                        navController.navigate("cart") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainPurple)
                ) {
                    Text("장바구니로", color = Color.White)
                }
            },
            dismissButton = {}
        )
    }
}

