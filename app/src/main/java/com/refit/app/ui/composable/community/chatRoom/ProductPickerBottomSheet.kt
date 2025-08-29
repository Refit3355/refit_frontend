package com.refit.app.ui.composable.community.chatRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/** 페이징 공용 모델 */
data class ListPage<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasNext: Boolean
)

data class ProductSummary(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val priceFormatted: String?,
    val discountRate: Int? = null,
    val discountedPriceFormatted: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPickerBottomSheet(
    onClose: () -> Unit,
    onSelect: (Long) -> Unit,
    loader: suspend (String, String?) -> ListPage<ProductSummary>,
    initialQuery: String = ""
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialQuery))
    }
    val listState = rememberLazyListState()

    var items by remember { mutableStateOf(listOf<ProductSummary>()) }
    var cursor by remember { mutableStateOf<String?>(null) }
    var hasNext by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    /** 페이지 로드 */
    suspend fun fetch(reset: Boolean) {
        if (isLoading) return
        if (!reset && (!hasNext)) return
        isLoading = true
        if (reset) isRefreshing = true
        val q = query.text.trim()
        val c = if (reset) null else cursor
        runCatching { loader(q, c) }
            .onSuccess { page ->
                if (reset) items = page.items else items = items + page.items
                cursor = page.nextCursor
                hasNext = page.hasNext
            }
            .onFailure {
                // 필요시 스낵바/토스트 처리
            }
        isLoading = false
        isRefreshing = false
    }

    // 최초/초기 검색
    LaunchedEffect(Unit) {
        fetch(reset = true)
    }

    // 쿼리 변경 → 디바운스 후 새 검색
    LaunchedEffect(query.text) {
        delay(250)
        fetch(reset = true)
    }

    // 스크롤 끝 근처에서 다음 페이지 자동 로드
    LaunchedEffect(listState, items, hasNext) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { lastVisible -> lastVisible >= items.lastIndex - 3 } // 끝에서 3개 남았을 때
            .distinctUntilChanged()
            .filter { it && hasNext && !isLoading }
            .collectLatest { fetch(reset = false) }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
        ) {
            Text(
                text = "상품 선택",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("상품명을 입력하세요") },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            if (isRefreshing && items.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(items, key = { it.id }) { p ->
                        ProductRow(
                            item = p,
                            onClick = { onSelect(p.id) }
                        )
                    }

                    // 바닥 로딩 인디케이터
                    if (isLoading && items.isNotEmpty()) {
                        item(key = "loading") {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(modifier = Modifier.size(22.dp)) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ProductRow(item: ProductSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shape = RoundedCornerShape(10.dp)
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(shape)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), shape)
                .background(Color(0xFFF9F9F9)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            val hasDiscount = (item.discountRate ?: 0) > 0 && !item.discountedPriceFormatted.isNullOrBlank()
            if (hasDiscount) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 할인율 칩
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${item.discountRate}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    // 할인가 (강조)
                    Text(
                        text = item.discountedPriceFormatted!!,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(8.dp))

                    // 정가 (취소선, 보조색)
                    item.priceFormatted?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            } else {
                // 할인 없을 때: 정가만
                item.priceFormatted?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
