package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartBadgeViewModel
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartOpType
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.mypage.MypageMenuSection
import com.refit.app.ui.composable.mypage.MypageProfileCard
import com.refit.app.ui.composable.mypage.RecentOrderSection
import com.refit.app.data.me.modelAndView.OrderViewModel
import com.refit.app.network.RetrofitInstance
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment

@Composable
fun MypageScreen(
    navController: NavController,
    vm: OrderViewModel = viewModel(),
    onCartChanged: () -> Unit
) {
    val state by vm.state.collectAsState()
    val api = RetrofitInstance.create(CartApi::class.java)
    val repo = remember { CartRepository(api) }
    val badgeVm = remember { CartBadgeViewModel(repo) }
    val cartVm = remember { CartEditViewModel(repo, badgeVm) }
    val scrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.loadOrders()
        cartVm.opEvents.collect { ev ->
            if (ev.type == CartOpType.ADD_ONE && ev.success) {
                onCartChanged()
            }
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end   = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                val nickname = UserPrefs.getNickname()
                val tags = UserPrefs.getTags()

                MypageProfileCard(
                    nickname = nickname ?: "사용자",
                    tags = tags,
                    onEditClick = { navController.navigate("account/health/edit") },
                    onArrowClick = { navController.navigate("account/edit") }
                )

                Spacer(Modifier.height(12.dp))

                val latestOrder = state.orders?.recentOrder
                    ?.maxByOrNull { order -> order.items.maxOfOrNull { it.createdAt } ?: "" }

                RecentOrderSection(
                    order = latestOrder,
                    onClickAll = { navController.navigate("orders") },
                    vm = vm,
                    cartVm = cartVm,
                    onCartChanged = onCartChanged,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
                Spacer(Modifier.height(12.dp))

                MypageMenuSection(navController = navController)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
