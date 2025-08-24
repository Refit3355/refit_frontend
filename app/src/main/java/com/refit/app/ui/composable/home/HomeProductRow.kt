package com.refit.app.ui.composable.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.local.wish.WishViewModel
import com.refit.app.data.product.model.Product
import com.refit.app.ui.composable.product.ProductCard
import androidx.compose.foundation.layout.heightIn

@Composable
fun HomeProductRow(
    products: List<Product>,
    modifier: Modifier = Modifier,
    onClick: (Product) -> Unit
) {
    val wishVM: WishViewModel = viewModel()
    val wished by wishVM.wishedIds.collectAsStateWithLifecycle(initialValue = emptySet())

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), // 카드 간격
        modifier = modifier.heightIn(min = 280.dp)
    ) {
        items(products) { p ->
            ProductCard(
                item = p,
                wished = p.id in wished,
                onToggleWish = { wishVM.toggle(p.id) },
                onClick = { onClick(p) },
                modifier = Modifier.width(160.dp) // 가로만 고정, 세로는 자동
            )
        }
    }
}
