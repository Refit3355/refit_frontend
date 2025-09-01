package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import com.refit.app.ui.composable.product.Cat
import com.refit.app.ui.composable.product.CategoryTabs
import com.refit.app.ui.composable.product.Group
import com.refit.app.ui.composable.product.GroupSegmented
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.R
import com.refit.app.data.product.modelAndView.ProductListViewModel
import com.refit.app.ui.composable.product.ProductGrid
import com.refit.app.ui.composable.product.SortBottomSheet
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CategoryScreen(
    navController: NavController,
    vm: ProductListViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showSortSheet by remember { mutableStateOf(false) }

    var selectedGroup by rememberSaveable { mutableStateOf(Group.BEAUTY) }
    var selectedCategoryIndex by rememberSaveable { mutableStateOf(0) }
    var selectedSortIndex by rememberSaveable { mutableStateOf(0) }

    val sortOptions = remember {
        listOf(
            "인기순" to "sales",
            "낮은 가격순" to "price_asc",
            "높은 가격순" to "price_desc",
            "신상품순" to "latest"
        )
    }

    val beautyCats = remember {
        listOf(
            Cat("전체", null),
            Cat("스킨/토너", 0),
            Cat("에센스/세럼/앰플", 1),
            Cat("크림", 2),
            Cat("로션/바디로션", 3),
            Cat("미스트", 4),
            Cat("오일", 5),
            Cat("샴푸/린스/트리트먼트", 6),
            Cat("헤어케어", 7)
        )
    }
    val healthCats = remember {
        listOf(
            Cat("전체", null),
            Cat("비타민", 8),
            Cat("오메가/루테인", 9),
            Cat("칼슘/마그네슘/철분", 10),
            Cat("유산균", 11)
        )
    }

    val categories = remember(selectedGroup) {
        if (selectedGroup == Group.BEAUTY) beautyCats else healthCats
    }

    LaunchedEffect(selectedGroup, selectedCategoryIndex, selectedSortIndex) {
        val groupParam = selectedGroup.param
        val sortParam = sortOptions[selectedSortIndex].second
        val categoryId: Int? = categories.getOrNull(selectedCategoryIndex)?.id
        vm.loadFirstPage(sort = sortParam, group = groupParam, limit = 20, category = categoryId)
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            // ===== 플로팅 버튼 스택 =====
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // 1) 챗봇 버튼
                FloatingActionButton(
                    onClick = { navController.navigate("chatbot") },
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = MainPurple,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    ),
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_jellbbo_chatbot_floating),
                        contentDescription = "챗봇",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 2) 성분 분석 버튼
                FloatingActionButton(
                    onClick = { navController.navigate("ingredient") },
                    shape = CircleShape,
                    containerColor = MainPurple,
                    contentColor = Color.White,
                    modifier = Modifier.size(60.dp)
                ) {
                    Text(
                        text = "성분\n분석",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = Pretendard,
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(Modifier.height(8.dp))

            GroupSegmented(
                selected = selectedGroup,
                onSelected = {
                    if (selectedGroup != it) {
                        selectedGroup = it
                        selectedCategoryIndex = 0
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 300.dp)
            )

            Spacer(Modifier.height(12.dp))

            CategoryTabs(
                tabs = categories.map { it.label },
                selectedIndex = selectedCategoryIndex,
                onSelect = { selectedCategoryIndex = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

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
                        withStyle(
                            SpanStyle(
                                color = MainPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) { append("${state.totalCount}") }
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

            ProductGrid(
                navController = navController,
                items = state.items,
                isLoading = state.isLoading,
                hasMore = state.hasMore,
                error = state.error,
                onLoadMore = { vm.loadNextPage() },
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
            onSelected = { i ->
                selectedSortIndex = i
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}