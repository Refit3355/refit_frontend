package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.refit.app.R
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartListViewModel
import com.refit.app.data.cart.modelAndView.CartOpType
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.network.RetrofitInstance
import com.refit.app.ui.composable.cart.CartItemRow
import com.refit.app.ui.composable.cart.CartSummarySection
import com.refit.app.ui.composable.cart.OrderBottomBar
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CartScreen(
    navController: NavController,
    onCartChanged: () -> Unit
) {
    // 1) API/Repo 1회 생성
    val api  = remember { RetrofitInstance.create(CartApi::class.java) }
    val repo = remember { CartRepository(api) }

    // 2) VM
    val listVm: CartListViewModel = viewModel(
        factory = viewModelFactory { initializer { CartListViewModel(repo) } }
    )
    val editVm: CartEditViewModel = viewModel(
        factory = viewModelFactory { initializer { CartEditViewModel(repo, badgeVm = null) } }
    )

    // 3) 상태
    val items   by listVm.items.collectAsStateWithLifecycle()
    val loading by listVm.loading.collectAsStateWithLifecycle()
    val error   by listVm.error.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<Set<Long>>(emptySet()) }

    val allIds = items.map { it.cartId }

    // 최초 로드
    LaunchedEffect(Unit) { listVm.load() }

    // 수량 변경/삭제 이벤트 처리
    LaunchedEffect(Unit) {
        editVm.opEvents.collect { ev ->
            if (!ev.success) return@collect
            when (ev.type) {
                CartOpType.UPDATE_QTY -> if (ev.cartId != null && ev.newQty != null)
                    listVm.applyLocalQuantity(ev.cartId, ev.newQty)
                CartOpType.DELETE_ONE -> ev.cartId?.let(listVm::removeLocal)
                CartOpType.DELETE_BULK -> ev.deletedIds?.forEach(listVm::removeLocal)
                else -> Unit
            }
            onCartChanged()
        }
    }

    // 총액/배송비 계산 (버튼과 요약에서 공통 사용)
    val subTotal = items
        .filter { it.cartId in selected }
        .sumOf { it.discountedPrice.toLong() * it.cartCnt }

    val FREE_SHIP_THRESHOLD = 50_000L
    val SHIPPING_FEE = 3_000L
    val shipping = when {
        subTotal == 0L -> 0L
        subTotal >= FREE_SHIP_THRESHOLD -> 0L
        else -> SHIPPING_FEE
    }
    val total = subTotal + shipping

    Scaffold(
        bottomBar = {
            OrderBottomBar(
                total = total,
                enabled = subTotal > 0L,
                onClick = {
                    // TODO: 주문 처리 로직 (선택 항목/총액 전달 등)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end   = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom= innerPadding.calculateBottomPadding()
                )
                .background(Color.White)
        ) {
            // 상단 선택/삭제 UI
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                val allSelected = selected.size == allIds.size && allIds.isNotEmpty()
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            selected = if (allSelected) emptySet() else allIds.toSet()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (allSelected) R.drawable.ic_icon_checked else R.drawable.ic_icon_unchecked
                        ),
                        contentDescription = "전체선택",
                        tint = MainPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("전체선택",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    fontSize = 16.sp
                )

                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .border(
                            width = 1.3.dp,
                            color = if (selected.isNotEmpty()) MainPurple else Color(0xFFB4B4B4),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp) // 텍스트 주변 원하는 만큼만 패딩
                        .clickable(enabled = selected.isNotEmpty()) {
                            editVm.deleteBulk(selected.toList())
                            selected = emptySet()
                        }
                ) {
                    Text(
                        "선택삭제",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(500),
                        fontSize = 14.sp,
                        color = if (selected.isNotEmpty()) MainPurple else Color(0xFFB4B4B4)
                    )
                }


            }

            // 리스트 + 요약을 리스트의 마지막 item으로 배치
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(LightPurple)
            ) {
                when {
                    loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    error != null -> Text(
                        text = error ?: "오류가 발생했습니다",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 10.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(items, key = { it.cartId }) { item ->
                                CartItemRow(
                                    item = item,
                                    checked = selected.contains(item.cartId),
                                    onCheckChanged = {
                                        selected = selected.toMutableSet().apply {
                                            if (!add(item.cartId)) remove(item.cartId)
                                        }
                                    },
                                    onMinus = {
                                        if (item.cartCnt > 1)
                                            editVm.updateQuantity(item.cartId, item.cartCnt - 1)
                                    },
                                    onPlus = { editVm.updateQuantity(item.cartId, item.cartCnt + 1) },
                                    onRemove = { editVm.deleteOne(item.cartId) }
                                )
                            }

                            item(key = "summary-spacer") {
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // 스크롤 맨 아래에 요약 노출
                            item {
                                CartSummarySection(
                                    items = items,
                                    selected = selected,
                                    freeShipThreshold = FREE_SHIP_THRESHOLD,
                                    shippingFee = SHIPPING_FEE
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
