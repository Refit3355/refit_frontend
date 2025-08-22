package com.refit.app.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.myfit.model.MemberProductItem
import com.refit.app.data.myfit.model.PurchasedProductDto
import com.refit.app.data.myfit.viewmodel.*
import com.refit.app.ui.composable.myfit.*
import com.refit.app.ui.composable.product.Group
import com.refit.app.ui.composable.product.GroupSegmented
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.PurpleBG
import kotlinx.coroutines.launch

@Composable
fun MyfitScreen(
    parentPadding: PaddingValues = PaddingValues(),
    navController: NavController? = null,
    onRecommendClick: (MemberProductItem) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val vm: MyfitViewModel = viewModel()
    val ui = vm.ui
    val scope = rememberCoroutineScope()

    var selectedGroup by rememberSaveable { mutableStateOf(Group.BEAUTY) }
    var selectedCategoryIndex by rememberSaveable { mutableStateOf(0) }

    //  등록/수정 화면에서 popBackStack 전에 set("myfit_refresh", true) 해두면 여기서 자동 새로고침
    val refreshSignal = remember(navController) {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("myfit_refresh", false)
    }?.collectAsState()

    LaunchedEffect(refreshSignal?.value) {
        if (refreshSignal?.value == true) {
            vm.refresh()
            navController?.currentBackStackEntry?.savedStateHandle?.set("myfit_refresh", false)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(parentPadding)
    ) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(8.dp))

            // 뷰티/헬스
            GroupSegmented(
                selected = selectedGroup,
                onSelected = {
                    if (selectedGroup != it) {
                        selectedGroup = it
                        selectedCategoryIndex = 0
                        vm.onTypeChange(
                            if (it == Group.BEAUTY) MyfitType.BEAUTY else MyfitType.HEALTH
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 300.dp)
            )

            MyfitStatusTabs(selected = ui.tab, onSelect = vm::onTabChange)

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(PurpleBG)
            ) {
                when {
                    ui.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    ui.error != null -> {
                        MyfitErrorPlaceholder(kind = ui.error!!.kind, onRetry = vm::refresh)
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .consumeWindowInsets(parentPadding),
                            contentPadding = PaddingValues(
                                start = 18.dp, end = 18.dp,
                                top = 0.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 0.dp, end = 0.dp, bottom = 12.dp)
                                ) {
                                    MyfitBanner(ui)
                                }
                            }

                            when (ui.tab) {
                                MyfitTab.REGISTER -> {
                                    if (ui.purchased.isEmpty()) {
                                        item {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 48.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "아래 + 버튼으로 제품을 등록해보세요.",
                                                    color = LocalContentColor.current.copy(alpha = 0.6f)
                                                )
                                            }
                                        }
                                    } else {
                                        items(ui.purchased, key = { it.orderItemId }) { p ->
                                            MyfitProductCard(
                                                item = p,
                                                onStartUsing = {
                                                    scope.launch {
                                                        vm.askUsing(p.orderItemId)
                                                        vm.refresh()
                                                    }
                                                },
                                                onClickItem = { goDetail(p, navController) }
                                            )
                                        }
                                    }
                                }

                                MyfitTab.COMPLETED -> {
                                    if (ui.items.isEmpty()) {
                                        item {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 48.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("사용 완료한 제품이 없습니다.", color = LocalContentColor.current.copy(alpha = 0.6f))
                                            }
                                        }
                                    } else {
                                        items(ui.items, key = { it.memberProductId }) { item ->
                                            MyfitCompletedCard(
                                                item = item,
                                                onRecommend = { /* TODO */ },
                                                onClickItem = { goDetailIfSell(item, navController) }
                                            )
                                        }
                                    }
                                }

                                MyfitTab.USING -> {
                                    if (ui.items.isEmpty()) {
                                        item {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 48.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("사용중인 제품이 없습니다.", color = LocalContentColor.current.copy(alpha = 0.6f))
                                            }
                                        }
                                    } else {
                                        items(ui.items, key = { it.memberProductId }) { item ->
                                            SwipeRevealBox(
                                                onComplete = { vm.askComplete(item.memberProductId) },
                                                onEdit     = { navController?.navigate("myfit/edit/${item.memberProductId}") },
                                                onDelete   = { vm.askDelete(item.memberProductId) }
                                            ) {
                                                MyfitUsingCard(
                                                    item = item,
                                                    onRecommend = { /* TODO */ },
                                                    onClickItem = { goDetailIfSell(item, navController) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item { Spacer(Modifier.height(50.dp)) }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController?.navigate("myfit/register") },
            shape = CircleShape,
            containerColor = MainPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }

        // 로그: 렌더 스냅샷
        LaunchedEffect(ui.items, ui.purchased) {
            Log.d("MyfitScreen", "render using/completed=${ui.items.size} register=${ui.purchased.size}")
        }

        // 다이얼로그: 확인 시 API → 끝나면 리스트 새로고침
        ui.confirmCompleteId?.let {
            ConfirmDialog(
                title = "사용 완료 처리 하시겠습니까?",
                text = "처리 후, 상태 변경이 불가합니다.",
                confirmText = "사용 완료",
                onDismiss = { vm.askComplete(null) },
                onConfirm = {
                    scope.launch {
                        vm.confirmComplete()
                        vm.refresh()
                    }
                }
            )
        }
        ui.confirmDeleteId?.let {
            ConfirmDialog(
                title = "삭제하시겠습니까?",
                text = "해당 항목은 복구할 수 없습니다.",
                confirmText = "삭제",
                onDismiss = { vm.askDelete(null) },
                onConfirm = {
                    scope.launch {
                        vm.confirmDelete()
                        vm.refresh()
                    }
                }
            )
        }
        ui.confirmUsingOrderItemId?.let {
            ConfirmDialog(
                title = "사용 등록 처리 하시겠습니까?",
                text = "처리 후, 상태 변경이 불가합니다.",
                confirmText = "사용 등록",
                onDismiss = { vm.askUsing(null) },
                onConfirm = vm::confirmUsing
            )
        }

    }
}

private fun goDetailIfSell(item: MemberProductItem, navController: NavController?) {
    val pid = item.productId ?: return
    navController?.navigate("product/$pid")
}

private fun goDetail(item: PurchasedProductDto, navController: NavController?) {
    navController?.navigate("product/${item.productId}")
}
