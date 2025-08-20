package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartListViewModel
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

    // 최초 로드
    LaunchedEffect(Unit) { listVm.load() }

    // 수정/삭제/추가 성공 시 목록 새로고침
    // 성공 이벤트 수신 → 메인에 알림
    LaunchedEffect(Unit) {
        editVm.opEvents.collect { ev ->
            if (ev.success) {
                onCartChanged()
                // 필요시 목록 리로드
                listVm.load()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
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
