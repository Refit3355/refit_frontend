package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.refit.app.R
import com.refit.app.data.chatbot.api.ChatbotApi                             // :contentReference[oaicite:4]{index=4}
import com.refit.app.data.chatbot.modelAndView.ProductViewModel            // :contentReference[oaicite:5]{index=5}
import com.refit.app.data.chatbot.repository.ChatbotRepositoryImpl         // :contentReference[oaicite:6]{index=6}
import com.refit.app.data.chatbot.usecase.GetProductsUseCase               // :contentReference[oaicite:7]{index=7}
import com.refit.app.network.RetrofitInstance
import com.refit.app.ui.composable.product.ProductGrid
import com.refit.app.ui.composable.product.SortBottomSheet
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun ProductListScreen(
    navController: NavHostController,
    bhType: Int,
    effectId: Int,
    sort: String = "latest"
) {
    // 간단 DI 대체: Api → Repo → UseCase → ViewModel(factory)
    val api  = remember { RetrofitInstance.create(ChatbotApi::class.java) }                // :contentReference[oaicite:8]{index=8}
    val repo = remember { ChatbotRepositoryImpl(api) }                                     // :contentReference[oaicite:9]{index=9}
    val useCase = remember { GetProductsUseCase(repository = repo) }                       // :contentReference[oaicite:10]{index=10}
    val vm: ProductViewModel = viewModel(factory = remember { ProductViewModelFactory(useCase) }) // :contentReference[oaicite:11]{index=11}

    val state by vm.uiState.collectAsState()
    var currentSort by rememberSaveable { mutableStateOf(sort) }

    // 최초 로드: bhType/effectId를 기존 VM 시그니처(templateId + effectCode)에 매핑
    LaunchedEffect(bhType, effectId, sort) {
        val templateId = when (bhType) {
            1 -> "reco_health_select"                 // 건강
            else -> if (effectId in 8..10) "reco_hair_select" else "reco_skin_select" // 피부/헤어
        }
        vm.fetchFirst(templateId = templateId, effectCode = effectId, sort = currentSort) // 내부에서 UseCase 호출
    }

    // 정렬(라벨, 코드) — VM에 정렬 지원 추가 전까지 UI만 유지
    val sortOptions = remember {
        listOf(
            "최신순" to "latest",
            "인기순" to "popular",
            "낮은 가격순" to "lowPrice",
            "높은 가격순" to "highPrice"
        )
    }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSortIndex by remember(sort) {
        mutableStateOf(sortOptions.indexOfFirst { it.second == sort }.coerceAtLeast(0))
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 상단: 총 개수 + 정렬 버튼
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    buildAnnotatedString {
                        append("총 ")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.SemiBold)) {
                            append("${state.totalCount}")
                        }
                        append("개의 상품")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.sp
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showSortSheet = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sortOptions.getOrNull(selectedSortIndex)?.first ?: "정렬",
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

            // 그리드 (끝에서 onLoadMore 트리거)
            ProductGrid(
                navController = navController,
                items = state.items,                 // contentReference[oaicite:12]{index=12}
                isLoading = state.isLoading,
                hasMore = state.hasMore,
                error = state.error,
                onLoadMore = {
                    val templateId = when (bhType) {
                        1 -> "reco_health_select"
                        else -> if (effectId in 8..10) "reco_hair_select" else "reco_skin_select"
                    }
                    vm.fetchMore(templateId = templateId, effectCode = effectId, sort = currentSort)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }

    if (showSortSheet) {
        SortBottomSheet(
            options = sortOptions,
            selectedIndex = selectedSortIndex,
            onSelected = { idx ->
                selectedSortIndex = idx
                showSortSheet = false
                val newSort = sortOptions[idx].second
                currentSort = newSort
                val templateId = when (bhType) {
                    1 -> "reco_health_select"
                    else -> if (effectId in 8..10) "reco_hair_select" else "reco_skin_select"
                }
                vm.fetchFirst(templateId = templateId, effectCode = effectId, sort = newSort)
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

/* ───────────────────── ViewModel Factory ───────────────────── */

private class ProductViewModelFactory(
    private val useCase: GetProductsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ProductViewModel::class.java))
        return ProductViewModel(useCase) as T
    }
}
