package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.mypage.MypageMenuSection
import com.refit.app.ui.composable.mypage.MypageProfileCard
import com.refit.app.ui.composable.mypage.RecentOrderSection
import com.refit.app.data.me.modelAndView.OrderViewModel

@Composable
fun MypageScreen(navController: NavController, vm: OrderViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadOrders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val nickname = UserPrefs.getNickname()
        val health = UserPrefs.getHealth()
        val tags = health?.toTags().orEmpty().take(2)

        MypageProfileCard(
            nickname = nickname ?: "사용자",
            tags = tags,
            onEditClick = { navController.navigate("account/health/edit") },
            onArrowClick = { navController.navigate("account/edit") }
        )

        Spacer(Modifier.height(12.dp))

        // 최신 주문 1건
        state.orders?.recentOrder
            ?.maxByOrNull { order -> order.items.maxOfOrNull { it.createdAt } ?: "" }
            ?.let { latestOrder ->
                RecentOrderSection(
                    order = latestOrder,
                    onClickAll = { navController.navigate("orders") },
                    vm = vm
                )
                Spacer(Modifier.height(12.dp))
            }

        MypageMenuSection(navController = navController)
    }
}
