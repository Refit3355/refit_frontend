package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.refit.app.data.local.search.SearchHistoryStore
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.modelAndView.SearchViewModel
import com.refit.app.data.product.modelAndView.SearchViewModelFactory
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun ProductSelectScreen(
    navController: NavController,
    bhType: String? = null,
    onConfirm: (List<Product>) -> Unit = {}
) {
    val context = LocalContext.current

    val vm: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(SearchHistoryStore(context))
    )

    LaunchedEffect(bhType) {
        vm.updateBhType(bhType)
    }

    LaunchedEffect(Unit) {
        vm.submitSearch()
    }

    val uiState by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedProducts by remember { mutableStateOf(listOf<Product>()) }
    val isValid = selectedProducts.size in 2..6

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .filterNotNull()
            .collectLatest { lastIndex ->
                if (lastIndex >= uiState.items.lastIndex &&
                    uiState.hasMore &&
                    !uiState.isLoading
                ) {
                    vm.loadMore()
                }
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedProducts", selectedProducts)
                    navController.popBackStack()
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) MainPurple else Color.LightGray
                )
            ) {
                Text(
                    "선택완료",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Pretendard
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {

            // 선택된 상품 미리보기
            if (selectedProducts.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedProducts) { product ->
                        Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.TopEnd) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box {
                                    AsyncImage(
                                        model = product.image,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "삭제",
                                        tint = Color.DarkGray,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .clickable {
                                                selectedProducts = selectedProducts.filter { it.id != product.id }
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.name,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = Pretendard
                                )
                            }
                        }
                    }
                }
            }

            // 검색창
            OutlinedTextField(
                value = uiState.query,
                onValueChange = {
                    vm.updateQuery(it)
                    vm.submitSearch()
                },
                placeholder = { Text("상품명을 입력하세요.", fontSize = 14.sp, fontFamily = Pretendard) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "검색", tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                singleLine = true,
            )

            // 검색 결과 리스트
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.items) { product ->
                    val isChecked = selectedProducts.any { it.id == product.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .clickable {
                                if (isChecked) {
                                    selectedProducts = selectedProducts.filter { it.id != product.id }
                                } else if (selectedProducts.size < 6) {
                                    selectedProducts = selectedProducts + product
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("상품은 최대 6개까지 선택할 수 있습니다.")
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                if (isChecked) {
                                    selectedProducts = selectedProducts.filter { it.id != product.id }
                                } else if (selectedProducts.size < 6) {
                                    selectedProducts = selectedProducts + product
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("상품은 최대 6개까지 선택할 수 있습니다.")
                                    }
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MainPurple)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(model = product.image, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = product.name,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = Pretendard
                        )
                    }
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MainPurple)
                        }
                    }
                }
            }
        }
    }
}
