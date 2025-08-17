package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.product.modelAndView.ProductDetailViewModel
import com.refit.app.ui.composable.productDetail.DetailBottomBar
import com.refit.app.ui.composable.productDetail.OrderBottomSheet
import com.refit.app.ui.composable.productDetail.ProductDetailBody
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

enum class SheetMode { CART, BUY }

@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavController,
    vm: ProductDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(productId) { vm.load(productId) }

    // 시트/수량/모드/스낵바 상태
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var sheetMode by rememberSaveable { mutableStateOf(SheetMode.CART) }
    var qty by rememberSaveable { mutableStateOf(1) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            DetailBottomBar(
                wished = false,
                onToggleWish = { /* TODO: 찜 토글 */ },
                onAddCart = {
                    sheetMode = SheetMode.CART
                    showSheet = true
                },
                onBuyNow = {
                    sheetMode = SheetMode.BUY
                    showSheet = true
                }
            )
        }
    ) { innerPadding ->

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error ?: "에러 발생")
            }
            state.data != null -> ProductDetailBody(
                detail = state.data!!,
                contentPadding = innerPadding
            )
        }

        // 바텀시트
        if (showSheet && state.data != null) {
            OrderBottomSheet(
                name = state.data!!.name,
                price = state.data!!.discountedPrice,
                originalPrice = state.data!!.price,
                qty = qty,
                onQtyChange = { qty = it.coerceIn(1, 99) },
                primaryLabel = if (sheetMode == SheetMode.CART) "장바구니 담기" else "바로 구매",
                onPrimary = {
                    // TODO: 실제 API 연결
                    scope.launch {
                        val msg = if (sheetMode == SheetMode.CART) "장바구니에 담았어요." else "구매 플로우로 이동합니다."
                        snackbarHostState.showSnackbar(msg)
                    }
                    showSheet = false
                },
                onDismiss = { showSheet = false }
            )
        }
    }
}


fun formatWon(value: Int): String =
    NumberFormat.getNumberInstance(Locale.KOREA).format(value) + "원"

fun formatRecommended(raw: String?): String {
    val days = raw?.toIntOrNull() ?: return "-"
    val months = kotlin.math.max(1, (days / 30.0).let { kotlin.math.round(it).toInt() })
    return if (months <= 1) "${days}일" else "${months}개월"
}
