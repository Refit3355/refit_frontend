package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartBadgeViewModel
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartOpType
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.me.modelAndView.OrderUiEvent
import com.refit.app.data.me.modelAndView.OrderViewModel
import com.refit.app.network.RetrofitInstance
import com.refit.app.ui.composable.mypage.CancelSuccessDialog
import com.refit.app.ui.composable.mypage.PartialCancelErrorDialog
import com.refit.app.ui.composable.mypage.OrderItemRow
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun OrderListScreen(
    navController: NavController,
    vm: OrderViewModel = viewModel(),
    onCartChanged: () -> Unit
) {
    val state by vm.state.collectAsState()
    val api = RetrofitInstance.create(CartApi::class.java)
    val repo = remember { CartRepository(api) }
    val badgeVm = remember { CartBadgeViewModel(repo) }
    val cartVm = remember { CartEditViewModel(repo, badgeVm) }

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showPartialCancelError by remember { mutableStateOf(false) }
    var showCancelSuccess by remember { mutableStateOf(false) }
    var cancelSuccessMessage by remember { mutableStateOf("결제 취소 신청되었습니다.") }

    LaunchedEffect(Unit) {
        vm.loadOrders()
        cartVm.opEvents.collect { ev ->
            if (ev.type == CartOpType.ADD_ONE && ev.success) {
                onCartChanged()
            }
        }
        vm.uiEvent.collect { ev ->
            when (ev) {
                is OrderUiEvent.PartialCancelFailed -> {
                    showPartialCancelError = true
                }
                is OrderUiEvent.CancelRequestSucceeded -> {
                    cancelSuccessMessage = ev.message.ifBlank { "결제 취소 신청되었습니다." }
                    showCancelSuccess = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.orders != null -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightPurple),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.orders!!.recentOrder) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val firstItem = order.items.firstOrNull()
                                if (firstItem != null) {
                                    val date = firstItem.createdAt.take(10).replace("-", ".")
                                    Text(date, fontFamily = Pretendard, fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp, color = Color.Black)
                                    Text("주문번호 ${firstItem.orderCode}", fontFamily = Pretendard,
                                        fontSize = 13.sp, color = Color.Gray)
                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                                    Spacer(Modifier.height(12.dp))
                                }

                                order.items.forEach { item ->
                                    OrderItemRow(
                                        item = item,
                                        vm = vm,
                                        cartVm = cartVm,
                                        onCartChanged = onCartChanged,
                                        navController = navController,
                                        snackbarHostState = snackbarHostState,
                                        scope = scope
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }

    // 에러 모달
    PartialCancelErrorDialog(
        visible = showPartialCancelError,
        onConfirm = { showPartialCancelError = false }
    )

    //  성공 모달
    CancelSuccessDialog(
        visible = showCancelSuccess,
        message = cancelSuccessMessage,
        onConfirm = {
            showCancelSuccess = false
            // 필요하면 목록 갱신
            vm.loadOrders()
        }
    )
}
