package com.refit.app.ui.screen.myfit

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.myfit.model.MemberProductItem
import com.refit.app.data.myfit.model.PurchasedProductDto
import com.refit.app.data.myfit.viewmodel.*
import com.refit.app.data.product.mapper.toProduct
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
    val context = LocalContext.current

    var selectedGroup by rememberSaveable { mutableStateOf(Group.BEAUTY) }
    var selectedCategoryIndex by rememberSaveable { mutableStateOf(0) }

    // 등록/수정 완료 뒤 새로고침 트리거
    val refreshSignal = remember(navController) {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("myfit_refresh", false)
    }?.collectAsState()

    // 등록 완료 후 USING 탭으로 전환하기 위한 플래그
    val switchToUsing = remember(navController) {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("myfit_switch_to_using", false)
    }?.collectAsState()

    LaunchedEffect(refreshSignal?.value) {
        if (refreshSignal?.value == true) {
            vm.refresh()
            navController?.currentBackStackEntry?.savedStateHandle?.set("myfit_refresh", false)
        }
    }

    LaunchedEffect(switchToUsing?.value) {
        if (switchToUsing?.value == true) {
            vm.onTabChange(MyfitTab.USING)
            vm.refresh()
            navController?.currentBackStackEntry?.savedStateHandle?.set("myfit_switch_to_using", false)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(parentPadding)
    ) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(8.dp))

            GroupSegmented(
                selected = selectedGroup,
                onSelected = {
                    if (selectedGroup != it) {
                        selectedGroup = it
                        selectedCategoryIndex = 0
                        vm.onTypeChange(if (it == Group.BEAUTY) MyfitType.BEAUTY else MyfitType.HEALTH)
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
                    ui.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    ui.error != null -> MyfitErrorPlaceholder(kind = ui.error!!.kind, onRetry = vm::refresh)
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .consumeWindowInsets(parentPadding),
                            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 0.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, bottom = 12.dp)
                                ) { MyfitBanner(ui) }
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
                                                    vm.askUsing(p.orderItemId)
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
                                            ) { Text("사용 완료한 제품이 없습니다.", color = LocalContentColor.current.copy(alpha = 0.6f)) }
                                        }
                                    } else {
                                        items(ui.items, key = { it.memberProductId }) { item ->
                                            MyfitCompletedCard(
                                                item = item,
                                                onRecommend = { mpItem ->
                                                    scope.launch {
                                                        val recs = vm.fetchRecommendations(mpItem)
                                                        val products = recs.map { it.toProduct() }
                                                        navController?.currentBackStackEntry
                                                            ?.savedStateHandle
                                                            ?.set("recommendation_items", products)
                                                        navController?.navigate("recommendation/4")
                                                    }
                                                },
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
                                            ) { Text("사용중인 제품이 없습니다.", color = LocalContentColor.current.copy(alpha = 0.6f)) }
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
                                                    onRecommend = { mpItem ->
                                                        scope.launch {
                                                            val recs = vm.fetchRecommendations(mpItem)
                                                            val products = recs.map { it.toProduct() }
                                                            navController?.currentBackStackEntry
                                                                ?.savedStateHandle
                                                                ?.set("recommendation_items", products)
                                                            navController?.navigate("recommendation/5")
                                                        }
                                                    },
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
        ) { Icon(Icons.Default.Add, contentDescription = null) }

        LaunchedEffect(ui.items, ui.purchased) {
            Log.d("MyfitScreen", "render using/completed=${ui.items.size} register=${ui.purchased.size}")
        }

        //  확인 다이얼로그
        ui.confirmCompleteId?.let {
            ConfirmDialog(
                title = "사용 완료 처리 하시겠습니까?",
                text = "처리 후, 상태 변경이 불가합니다.",
                confirmText = "사용 완료",
                onDismiss = { vm.askComplete(null) },
                onConfirm = {
                    scope.launch {
                        runCatching { vm.confirmComplete() }
                            .onSuccess {
                                Toast.makeText(context, "사용 완료 처리되었습니다.", Toast.LENGTH_SHORT).show()
                                vm.refresh()
                            }
                            .onFailure {
                                 Toast.makeText(context, "사용 완료 처리에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                vm.refresh()
                            }
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
                        runCatching { vm.confirmDelete() }
                            .onSuccess {
                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                vm.refresh()
                            }
                            .onFailure {
                                 Toast.makeText(context, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                vm.refresh()
                            }
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
                onConfirm = {
                    scope.launch {
                        runCatching { vm.confirmUsing() }
                            .onSuccess {
                                Toast.makeText(context, "사용 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                vm.onTabChange(MyfitTab.USING)
                                vm.refresh()
                            }
                    }
                }
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
