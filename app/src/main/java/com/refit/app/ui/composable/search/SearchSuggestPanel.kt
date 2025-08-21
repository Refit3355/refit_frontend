package com.refit.app.ui.composable.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refit.app.data.product.model.Product
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.Pretendard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSuggestPanel(
    recent: List<String>,
    onClickRecent: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
    popular: List<Product>,
    loading: Boolean,
    onClickPopular: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val titleStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        color = DarkBlack
    )

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 최근 검색어 섹션 (있을 때만)
        if (recent.isNotEmpty()) {
            item {
                Text("최근 검색어", style = titleStyle)
                Spacer(Modifier.height(8.dp))
            }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recent.forEach { q ->
                        RecentChip(
                            text = q,
                            onClick = { onClickRecent(q) },    // 채우고 + 즉시 검색
                            onRemove = { onRemoveRecent(q) }   // 삭제
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }

        // 인기상품 섹션 타이틀
        item {
            Text("실시간 판매 BEST", style = titleStyle)
            Spacer(Modifier.height(8.dp))
        }

        // 로딩/목록
        if (loading) {
            item {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
            }
        } else {
            itemsIndexed(popular.take(10)) { index, p ->
                PopularRankItem(
                    rank = index + 1,
                    product = p,
                    onClick = { onClickPopular(p.id) }
                )
                Divider()
            }
        }
    }
}
