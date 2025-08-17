package com.refit.app.ui.composable.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refit.app.data.product.model.Product
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun ProductGrid(
    items: List<Product>,
    isLoading: Boolean,
    hasMore: Boolean,
    error: String?,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    // 무한 스크롤 트리거 내부 캡슐화
    LaunchedEffect(gridState, isLoading, hasMore) {
        snapshotFlow { gridState.layoutInfo }
            .map { info -> (info.visibleItemsInfo.lastOrNull()?.index ?: -1) to info.totalItemsCount }
            .distinctUntilChanged()
            .collectLatest { (last, total) ->
                if (total > 0 && last >= total - 4 && !isLoading && hasMore) onLoadMore()
            }
    }

    Box(modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            contentPadding = PaddingValues(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items.size) { i ->
                ProductCard(
                    item = items[i],
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (isLoading && items.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
        }

        if (isLoading && items.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        error?.let { Text(text = it, modifier = Modifier.align(Alignment.Center)) }
    }
}
