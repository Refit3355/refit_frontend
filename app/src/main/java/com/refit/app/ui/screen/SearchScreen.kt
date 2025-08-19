package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.refit.app.R
import com.refit.app.data.local.search.SearchHistoryStore
import com.refit.app.data.product.modelAndView.SearchMode
import com.refit.app.ui.composable.product.ProductGrid
import com.refit.app.ui.composable.product.SortBottomSheet
import com.refit.app.data.product.modelAndView.SearchViewModel
import com.refit.app.data.product.modelAndView.SearchViewModelFactory
import com.refit.app.ui.composable.search.SearchSuggestPanel
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(
            history = SearchHistoryStore(context)
        )
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val argQuery = remember(backStackEntry) {
        backStackEntry?.arguments?.getString("query")
    }

    // VM 상태 구독
    val state by vm.state.collectAsStateWithLifecycle()

    // 라우트 파라미터가 바뀔 때마다 검색 실행
    LaunchedEffect(argQuery) {
        if (!argQuery.isNullOrBlank()) {
            // (선택) 동일 쿼리면 생략
            if (state.query != argQuery) {
                vm.updateQuery(argQuery!!)
                vm.submitSearch()
            }
        } else {
            // 쿼리 없으면 Suggest 모드로
            vm.clearQuery()
        }
    }

    // 정렬 옵션 예시
    val sortOptions = listOf(
        "신상품순" to "latest",
        "인기순" to "sales",
        "낮은 가격순" to "price_asc",
        "높은 가격순" to "price_desc"
    )
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSortIndex by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        if (state.mode == SearchMode.Result){
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val countText = buildAnnotatedString {
                    append("총 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.SemiBold)) {
                        append("${state.totalCount}")
                    }
                    append("개의 상품")
                }
                Text(
                    countText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.sp
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showSortSheet = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sortOptions[selectedSortIndex].first,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_icon_sort),
                        contentDescription = "정렬",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 상품 그리드 (무한스크롤)
            ProductGrid(
                navController = navController,
                items = state.items,
                isLoading = state.isLoading,
                hasMore = state.hasMore,
                error = state.error,
                onLoadMore = { vm.loadMore() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            )
        }else{
            SearchSuggestPanel(
                recent = state.recentQueries,
                onClickRecent = { q ->
                    val encoded = android.net.Uri.encode(q)
                    navController.navigate("search?query=$encoded") {
                        launchSingleTop = true
                    }
                },
                onRemoveRecent = { q ->
                    vm.removeHistory(q) // DataStore에서 삭제 (VM 메서드)
                },
                popular = state.popular,
                loading = state.popularLoading,
                onClickPopular = { productId ->
                    navController.navigate("product/$productId")
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }

    if (showSortSheet) {
        SortBottomSheet(
            options = sortOptions,
            selectedIndex = selectedSortIndex,
            onSelected = { i ->
                selectedSortIndex = i
                showSortSheet = false
                vm.changeSort(sortOptions[i].second) // 정렬 변경 → 재조회
            },
            onDismiss = { showSortSheet = false }
        )
    }
}
