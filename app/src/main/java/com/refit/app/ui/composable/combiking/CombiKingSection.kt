package com.refit.app.ui.composable.combiking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.refit.app.data.local.combination.MyCombinationStore
import com.refit.app.data.combination.modelAndView.CombinationViewModel
import com.refit.app.data.combination.modelAndView.LikedCombinationViewModel
import com.refit.app.ui.composable.community.CommunityCategory
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.LayoutDirection
import com.refit.app.R
import com.refit.app.ui.composable.product.SortBottomSheet

@Composable
fun CombiKingSection(
    navController: NavController,
    category: CommunityCategory,
    vm: CombinationViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val myCombinationStore = remember { MyCombinationStore(context) }
    val savedIds by myCombinationStore.savedIds.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()
    val likedVm: LikedCombinationViewModel = viewModel()
    val likedState by likedVm.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // 정렬 옵션
    val sortOptions = listOf(
        "최신순" to "latest",
        "인기순" to "popular",
        "가격낮은순" to "lowPrice",
        "가격높은순" to "highPrice"
    )

    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSortIndex by remember { mutableStateOf(0) }
    val selectedSort = sortOptions[selectedSortIndex].second


    // 카테고리
    val type = when (category) {
        CommunityCategory.ALL -> "all"
        CommunityCategory.BEAUTY -> "beauty"
        CommunityCategory.HEALTH -> "health"
    }

    // 첫 로드
    LaunchedEffect(type, selectedSort) {
        vm.loadCombinations(type, selectedSort, limit = 10)
    }

    // 리스트 상태
    val listState = rememberLazyListState()

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val result = navBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("combination_result", null)
        ?.collectAsState()

    LaunchedEffect(result?.value) {
        result?.value?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
            navBackStackEntry?.savedStateHandle?.set("combination_result", null)
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    top = 0.dp,
                    bottom = 0.dp
                )
        ) {
            Column {
                // 상단 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("총 ", fontSize = 14.sp, fontFamily = Pretendard)
                        Text(
                            "${state.totalCount}",
                            color = MainPurple,
                            fontSize = 14.sp,
                            fontFamily = Pretendard
                        )
                        Text("개의 조합", fontSize = 14.sp, fontFamily = Pretendard)
                    }

                    // 우측 정렬 드롭다운
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showSortSheet = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = sortOptions[selectedSortIndex].first,
                                fontSize = 14.sp,
                                fontFamily = Pretendard,
                                color = Color.Black
                            )
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_icon_sort),
                                contentDescription = "정렬",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // 조합 목록
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.combinations) { combination ->
                        CombinationCard(
                            combination = combination,
                            isSaved = savedIds.contains(combination.combinationId),
                            onToggleSave = { id ->
                                scope.launch {
                                    val wasSaved = savedIds.contains(id)
                                    myCombinationStore.toggle(id)

                                    val updated = state.combinations.map {
                                        if (it.combinationId == id) {
                                            if (wasSaved) it.copy(likes = maxOf(it.likes - 1, 0))
                                            else it.copy(likes = it.likes + 1)
                                        } else it
                                    }
                                    vm.updateCombinations(updated)

                                    if (wasSaved) {
                                        likedVm.dislikeCombination(id)
                                    } else {
                                        likedVm.likeCombination(id)
                                    }
                                }
                            },
                            onClick = { id ->
                                navController.navigate("combinationDetail/$id")
                            }
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // 등록 버튼
            FloatingActionButton(
                onClick = {
                    val defaultType = when (category) {
                        CommunityCategory.ALL, CommunityCategory.BEAUTY -> "beauty"
                        CommunityCategory.HEALTH -> "health"
                    }
                    navController.navigate("combinationRegister/$defaultType")
                },
                shape = CircleShape,
                containerColor = MainPurple,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "등록 버튼",
                    modifier = Modifier.size(32.dp)
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
            )
        }
    }

    // 스크롤 끝 감지해서 무한 스크롤 처리
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisible ->
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisible == totalItems - 1 && state.hasMore && !state.isLoadingMore) {
                    vm.loadMoreCombinations(type, selectedSort, limit = 10)
                }
            }
    }

    if (showSortSheet) {
        SortBottomSheet(
            options = sortOptions,
            selectedIndex = selectedSortIndex,
            onSelected = { i ->
                selectedSortIndex = i
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }

}
