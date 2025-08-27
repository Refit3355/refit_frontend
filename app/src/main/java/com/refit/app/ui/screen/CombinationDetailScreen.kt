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

@Composable
fun CombinationDetailScreen(
    navController: NavController,
    combinationId: Long,
    vm: CombinationDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val pagerState = rememberPagerState { state.detail?.products?.size ?: 0 }

    // ✅ DataStore 연결
    val context = LocalContext.current
    val myCombinationStore = remember { MyCombinationStore(context) }
    val savedIds by myCombinationStore.savedIds.collectAsState(initial = emptySet())
    val isSaved = savedIds.contains(combinationId)

    val scope = rememberCoroutineScope()

    // 자동 슬라이드
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

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        state.detail?.let { detail ->
            Scaffold(
                containerColor = Color.White,
                bottomBar = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 저장 버튼
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(60.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(60.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp) // 원하는 사이즈
                                        .clip(RoundedCornerShape(4.dp)) // 클릭 영역 모양
                                        .clickable {
                                            if (isSaved) {
                                                scope.launch {
                                                    CombinationRepository().dislikeCombination(combinationId)
                                                    myCombinationStore.toggle(combinationId)
                                                }
                                            } else {
                                                scope.launch {
                                                    CombinationRepository().likeCombination(combinationId)
                                                    myCombinationStore.toggle(combinationId)
                                                }
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

                                Spacer(Modifier.height(2.dp)) // 👈 원하는 만큼만 간격 조정
                                Text(
                                    "저장",
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp, // 👈 lineHeight를 fontSize랑 맞추면 Text 자체 위/아래 여백도 최소화
                                    color = Color.Gray
                                )
                            }
                        }

                        // 장바구니 버튼
                        OutlinedButton(
                            modifier = Modifier.weight(1f).height(48.dp),
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = MainPurple
                            ),
                            shape = RoundedCornerShape(4.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MainPurple)
                            )
                        ) {
                            Text("장바구니")
                        }

                        // 구매하기 버튼
                        Button(
                            modifier = Modifier.weight(1f).height(48.dp),
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MainPurple),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("구매하기", color = Color.White)
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .background(Color.White)
                ) {
                    // 메인 이미지 자동 슬라이드
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
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

                    // 작은 상품 카드 리스트
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(detail.products) { product ->
                            Row(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LightPurple)
                                    .padding(6.dp),
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
                                    Text(
                                        text = product.brandName ?: "",
                                        fontSize = 10.sp,
                                        lineHeight = 10.sp,
                                        color = Color.Black,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = product.productName,
                                        fontSize = 11.sp,
                                        lineHeight = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        maxLines = 1
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "${product.discountRate}%",
                                            color = Color.Red,
                                            fontSize = 11.sp,
                                            lineHeight = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            "${product.discountedPrice}원",
                                            fontSize = 11.sp,
                                            lineHeight = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                    Spacer(Modifier.height(20.dp))

                    // 작성자 프로필 + 닉네임
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = detail.profileUrl,
                            contentDescription = "${detail.nickname} 프로필",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            detail.nickname,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // 조합명
                    Text(
                        detail.combinationName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(8.dp))

                    // 가격
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${detail.discountedTotalPrice}원",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${detail.originalTotalPrice}원",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Spacer(Modifier.height(12.dp))

                    Text(detail.combinationDescription, color = Color.Black)
                }
            }
        }
    }
}
