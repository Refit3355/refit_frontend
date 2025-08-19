package com.refit.app.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.local.wish.WishViewModel
import com.refit.app.data.me.modelAndView.LikedProductsViewModel
import com.refit.app.ui.composable.product.ProductGrid

@Composable
fun WishScreen(navController: NavController) {
    val likedVm: LikedProductsViewModel = viewModel()
    val wishVm: WishViewModel = viewModel()

    val wishedIds by wishVm.wishedIds.collectAsStateWithLifecycle(initialValue = emptySet())
    LaunchedEffect(wishedIds) {
        likedVm.refresh(wishedIds)
    }

    val ui by likedVm.state.collectAsStateWithLifecycle()

    ProductGrid(
        navController = navController,
        items = ui.items,
        isLoading = ui.isLoading,
        hasMore = false,
        error = ui.error,
        onLoadMore = {},
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    )
}