package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.refit.app.data.combination.modelAndView.CombinationDetailViewModel
import com.refit.app.data.combination.repository.CombinationRepository
import com.refit.app.data.local.combination.MyCombinationStore
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.model.CartAddRequest
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.cart.modelAndView.CartOpType
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.network.RetrofitInstance
import com.refit.app.util.common.PriceUtil
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.order.model.DraftOrderRequest
import com.refit.app.data.order.model.encodeDraftOrderRequest
import com.refit.app.data.order.model.OrderSource

@Composable
fun CombinationDetailScreen(
    navController: NavController,
    combinationId: Long,
    vm: CombinationDetailViewModel = viewModel(),
    onCartChanged: () -> Unit = {}
) {
    val state by vm.state.collectAsState()
    val pagerState = rememberPagerState { state.detail?.products?.size ?: 0 }

    val context = LocalContext.current
    val myCombinationStore = remember { MyCombinationStore(context) }
    val savedIds by myCombinationStore.savedIds.collectAsState(initial = emptySet())
    val isSaved = savedIds.contains(combinationId)

    val scope = rememberCoroutineScope()
    val cartApi = remember { RetrofitInstance.create(CartApi::class.java) }
    val repo = remember { CartRepository(cartApi) }
    val editVm: CartEditViewModel = viewModel(
        factory = viewModelFactory { initializer { CartEditViewModel(repo, badgeVm = null) } }
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pagerState.pageCount) {
        if (pagerState.pageCount > 1) {
            while (true) {
                delay(3000)
                val next = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(next)
            }
        }
    }

    LaunchedEffect(combinationId) {
        vm.loadCombinationDetail(combinationId)
    }

    LaunchedEffect(Unit) {
        editVm.opEvents.collect { ev ->
            if (ev.type == CartOpType.ADD_BULK) {
                if (ev.success) {
                    snackbarHostState.showSnackbar("장바구니에 담겼습니다.")
                    onCartChanged()
                } else {
                    snackbarHostState.showSnackbar(ev.message ?: "장바구니 추가 실패")
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 저장 버튼
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                scope.launch {
                                    if (isSaved) {
                                        CombinationRepository().dislikeCombination(combinationId)
                                    } else {
                                        CombinationRepository().likeCombination(combinationId)
                                    }
                                    myCombinationStore.toggle(combinationId)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "저장",
                            tint = if (isSaved) MainPurple else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text("저장", fontSize = 12.sp, lineHeight = 12.sp, color = Color.Gray, fontFamily = Pretendard)
                }

                // 장바구니 버튼
                OutlinedButton(
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = {
                        scope.launch {
                            try {
                                val items = state.detail?.products?.map { p ->
                                    CartAddRequest(productId = p.productId, quantity = 1)
                                } ?: emptyList()

                                if (items.isNotEmpty()) {
                                    editVm.addBulk(items)
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("장바구니 추가 실패: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = MainPurple
                    ),
                    shape = RoundedCornerShape(4.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MainPurple)
                    )
                ) {
                    Text("장바구니", fontFamily = Pretendard)
                }

                // 구매하기 버튼
                Button(
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = {
                        val draft = DraftOrderRequest(
                            source = OrderSource.COMBINATION,
                            combinationId = combinationId
                        )
                        val payload = encodeDraftOrderRequest(draft)
                        val encoded = Uri.encode(payload)

                        navController.navigate("checkout/orderSheet?payload=$encoded")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainPurple),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("구매하기", color = Color.White, fontFamily = Pretendard)
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            state.detail?.let { detail ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .background(Color.White)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth().height(260.dp)
                    ) { page ->
                        val product = detail.products[page]
                        AsyncImage(
                            model = product.thumbnailUrl,
                            contentDescription = product.productName,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(detail.products) { product ->
                            Row(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LightPurple)
                                    .padding(6.dp)
                                    .clickable {
                                        navController.navigate("product/${product.productId}")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = product.thumbnailUrl,
                                    contentDescription = product.productName,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                )
                                Spacer(Modifier.width(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(product.brandName ?: "", fontSize = 10.sp, lineHeight = 10.sp, color = Color.Black, maxLines = 1, fontFamily = Pretendard)
                                    Text(product.productName, fontSize = 11.sp, lineHeight = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, fontFamily = Pretendard)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "${product.discountRate}%",
                                            color = Color.Red,
                                            fontSize = 11.sp,
                                            lineHeight = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Pretendard
                                        )
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            PriceUtil.formatPrice(product.discountedPrice),
                                            fontSize = 11.sp,
                                            lineHeight = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black,
                                            fontFamily = Pretendard
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                    Spacer(Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = detail.profileUrl,
                            contentDescription = "${detail.nickname} 프로필",
                            modifier = Modifier.size(24.dp).clip(RoundedCornerShape(50)).background(Color.White)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(detail.nickname, style = MaterialTheme.typography.bodySmall, color = Color.Black, fontFamily = Pretendard)
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(detail.combinationName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.Black, fontFamily = Pretendard)
                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            PriceUtil.formatPrice(detail.discountedTotalPrice),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = Pretendard
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            PriceUtil.formatPrice(detail.originalTotalPrice),
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = Color.Gray,
                            fontFamily = Pretendard
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                    Spacer(Modifier.height(12.dp))
                    Text(detail.combinationDescription, color = Color.Black, fontFamily = Pretendard)
                }
            }
        }
    }
}
