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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.refit.app.R
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
            Surface(
                color = Color.White,
                tonalElevation = 1.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 찜하기
                    Column(
                        modifier = Modifier
                            .width(60.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                scope.launch {
                                    if (isSaved) {
                                        CombinationRepository().dislikeCombination(combinationId)
                                    } else {
                                        CombinationRepository().likeCombination(combinationId)
                                    }
                                    myCombinationStore.toggle(combinationId)
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(
                                if (isSaved) R.drawable.ic_bookmark_purple else R.drawable.ic_bookmark_basic
                            ),
                            contentDescription = "저장",
                            modifier = Modifier.height(24.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "저장",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = if (isSaved) MainPurple else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // 장바구니 (아웃라인)
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val items = state.detail?.products?.map { p ->
                                        CartAddRequest(productId = p.productId, quantity = 1)
                                    } ?: emptyList()
                                    if (items.isNotEmpty()) editVm.addBulk(items)
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("장바구니 추가 실패: ${e.message}")
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, MainPurple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MainPurple),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            "장바구니",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // 구매하기 (필드)
                    Button(
                        onClick = {
                            val draft = DraftOrderRequest(
                                source = OrderSource.COMBINATION,
                                combinationId = combinationId
                            )
                            val payload = encodeDraftOrderRequest(draft)
                            val encoded = Uri.encode(payload)
                            navController.navigate("checkout/orderSheet?payload=$encoded")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainPurple,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            "구매하기",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
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
                        .background(Color.White)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    ) { page ->
                        val product = detail.products[page]
                        AsyncImage(
                            model = product.thumbnailUrl,
                            contentDescription = product.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(detail.products) { product ->
                            Row(
                                modifier = Modifier
                                    .width(190.dp)
                                    .height(72.dp)
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
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                )
                                Spacer(Modifier.width(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(product.brandName ?: "", fontSize = 12.sp, lineHeight = 12.sp, color = Color(0xFF808080), maxLines = 1, fontFamily = Pretendard)
                                    Text(
                                        text = product.productName,
                                        fontSize = 14.sp,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFF3A3A3A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontFamily = Pretendard,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (product.discountRate > 0) {
                                            Text(
                                                "${product.discountRate}%",
                                                color = Color(0xFFFF0000),
                                                fontSize = 14.sp,
                                                lineHeight = 14.sp,
                                                fontWeight = FontWeight(500),
                                                fontFamily = Pretendard
                                            )
                                            Spacer(Modifier.width(2.dp))
                                        }
                                        Text(
                                            PriceUtil.formatPrice(product.discountedPrice),
                                            fontSize = 14.sp,
                                            lineHeight = 14.sp,
                                            fontWeight = FontWeight(500),
                                            color = Color(0xFF3A3A3A),
                                            fontFamily = Pretendard
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                    Spacer(Modifier.height(20.dp))

                    val contentInset = Modifier.padding(horizontal = 20.dp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = contentInset
                    ) {
                        AsyncImage(
                            model = detail.profileUrl,
                            contentDescription = "${detail.nickname} 프로필",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = detail.nickname,
                            fontSize = 15.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                            fontFamily = Pretendard
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        detail.combinationName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontFamily = Pretendard,
                        modifier = contentInset
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = contentInset
                    ) {
                        Text(
                            PriceUtil.formatPrice(detail.discountedTotalPrice),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontFamily = Pretendard
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            PriceUtil.formatPrice(detail.originalTotalPrice),
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = Color.Gray,
                            fontFamily = Pretendard,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                    Spacer(Modifier.height(20.dp))

                    Text(
                        detail.combinationDescription,
                        color = Color.Black,
                        fontFamily = Pretendard,
                        modifier = contentInset
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}
