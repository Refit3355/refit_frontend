package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartListViewModel
import com.refit.app.data.cart.modelAndView.CartOpType
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.network.RetrofitInstance
import com.refit.app.ui.composable.cart.CartItemRow

@Composable
fun CartScreen(
    navController: NavController,
    onCartChanged: () -> Unit
) {
    // 1) API/Repo를 화면 생명주기 동안 1회 생성
    val api  = remember { RetrofitInstance.create(CartApi::class.java) }
    val repo = remember { CartRepository(api) }

    // 2) VM 생성 (이 화면 스코프)
    val listVm: CartListViewModel = viewModel(
        factory = viewModelFactory { initializer { CartListViewModel(repo) } }
    )
    // 배지 갱신이 MainActivity에서만 필요하다면 badgeVm은 넘기지 않습니다 (null 허용으로 변경 권장)
    val editVm: CartEditViewModel = viewModel(
        factory = viewModelFactory { initializer { CartEditViewModel(repo, badgeVm = null) } }
    )

    // 3) 상태 수집
    val items   by listVm.items.collectAsStateWithLifecycle()
    val loading by listVm.loading.collectAsStateWithLifecycle()
    val error   by listVm.error.collectAsStateWithLifecycle()

    // 4) 선택 상태(이 화면 내부에서만 쓰면 로컬 state로 충분)
    var selected by remember { mutableStateOf<Set<Long>>(emptySet()) }

    // 상품 전체 ID 리스트
    val allIds = items.map { it.cartId }

    // 최초 로드
    LaunchedEffect(Unit) { listVm.load() }

    // 수량 변경 및 삭제시 상태 변경
    LaunchedEffect(Unit) {
        editVm.opEvents.collect { ev ->
            if (!ev.success) {
                // 실패 메시지 처리만 하고 종료
                return@collect
            }

            when (ev.type) {
                CartOpType.UPDATE_QTY -> {
                    if (ev.cartId != null && ev.newQty != null) {
                        listVm.applyLocalQuantity(ev.cartId, ev.newQty)
                    }
                }
                CartOpType.DELETE_ONE -> {
                    ev.cartId?.let { listVm.removeLocal(it) }
                }
                CartOpType.DELETE_BULK -> {
                    ev.deletedIds?.forEach { listVm.removeLocal(it) }
                }
                CartOpType.ADD_ONE -> {
                    // 장바구니 화면에서 직접 추가하는 흐름이 있다면,
                    // ev에 새 아이템을 실어 addLocal(item) 호출 (아니면 생략)
                }
                CartOpType.ADD_BULK -> {
                    // 동일
                }
            }

            // 배지 갱신 신호만 올림 (네트워크 재조회 X)
            onCartChanged()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 선택/삭제 UI
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            // 전체선택 체크박스
            val allSelected = selected.size == allIds.size && allIds.isNotEmpty()
            Checkbox(
                checked = allSelected,
                onCheckedChange = { checked ->
                    selected = if (checked) allIds.toSet() else emptySet()
                }
            )
            Spacer(Modifier.width(8.dp))
            Text("전체 선택")

            Spacer(Modifier.weight(1f))

            Button(
                enabled = selected.isNotEmpty(),
                onClick = {
                    editVm.deleteBulk(selected.toList())
                    selected = emptySet() // 선택 초기화
                }
            ) {
                Text("선택삭제")
            }
        }

        // 기존 목록 표시 부분
        Box(modifier = Modifier.weight(1f)) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(
                    text = error ?: "오류가 발생했습니다",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
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
                    }
                }
            }
        }
    }
}