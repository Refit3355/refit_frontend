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
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartOpType
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.product.modelAndView.ProductDetailViewModel
import com.refit.app.ui.composable.productDetail.DetailBottomBar
import com.refit.app.ui.composable.productDetail.OrderBottomSheet
import com.refit.app.ui.composable.productDetail.ProductDetailBody
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import com.refit.app.data.local.wish.WishViewModel
import com.refit.app.network.RetrofitInstance


enum class SheetMode { CART, BUY }

@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavController,
    vm: ProductDetailViewModel = viewModel(),
    onCartChanged: () -> Unit = {}
) {
    val state by vm.state.collectAsStateWithLifecycle()

    val api  = remember { RetrofitInstance.create(CartApi::class.java) }
    val repo = remember { CartRepository(api) }
    val editVm: CartEditViewModel = viewModel(
        factory = viewModelFactory { initializer { CartEditViewModel(repo, badgeVm = null) } }
    )

    val wishVm: WishViewModel = viewModel()
    val wishedIds by wishVm.wishedIds.collectAsStateWithLifecycle()
    val isWished = remember(productId, wishedIds) { (productId.toLong() in wishedIds) }

    LaunchedEffect(productId) { vm.load(productId) }

    // 시트/수량/모드/스낵바 상태
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var sheetMode by rememberSaveable { mutableStateOf(SheetMode.CART) }
    var qty by rememberSaveable { mutableStateOf(1) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // 장바구니 추가/실패 이벤트 처리
    LaunchedEffect(Unit) {
        editVm.opEvents.collect { ev ->
            when (ev.type) {
                CartOpType.ADD_ONE -> {
                    if (ev.success) {
                        scope.launch {
                            // 이미 떠 있는 스낵바가 있으면 닫기
                            snackbarHostState.currentSnackbarData?.dismiss()

                            val result = snackbarHostState.showSnackbar(
                                message = "장바구니에 담았어요.",
                                actionLabel = "바로가기",          // 우측 액션 버튼 라벨
                                withDismissAction = true,          // 닫기 액션 표시
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                navController.navigate("cart")
                            }
                        }
                        onCartChanged()          // 전역 배지 갱신
                        showSheet = false
                    } else {
                        val msg = ev.message ?: "추가 실패"
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                        // 필요 시: 비로그인 → 로그인 화면 이동
                        if (msg.contains("로그인")) {
                            navController.navigate("auth/login")
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            DetailBottomBar(
                wished = isWished,
                onToggleWish = { wishVm.toggle(productId.toLong()) },
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
                    if (sheetMode == SheetMode.CART) {
                        editVm.addOne(productId.toLong(), qty)
                    } else {
                        // 바로구매 플로우(주문 화면 이동 등)
//                        navController.navigate("order/confirm?productId=$productId&qty=$qty")
//                        showSheet = false
                    }
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
