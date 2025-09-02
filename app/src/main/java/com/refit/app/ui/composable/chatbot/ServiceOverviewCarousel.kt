package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.refit.app.R
import com.refit.app.ui.theme.Pretendard

data class CarouselAction(val label: String, val next: String)
data class CarouselCard(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val actions: List<CarouselAction>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ServiceOverviewCarousel(
    onNext: (String) -> Unit,
    resetKey: Any
) {
    // (맞춤형 추천, 마이핏) / (매칭률, 성분 분석) / (그룹채팅, 조합왕)
    val cards = remember {
        listOf(
            CarouselCard(
                title = "상품 추천",
                subtitle = "recommend",
                imageRes = R.drawable.jellbbo_reco, // 임시 이미지
                actions = listOf(
                    CarouselAction("맞춤형 추천", "service_reco"),
                    CarouselAction("마이핏",   "service_myfit")
                )
            ),
            CarouselCard(
                title = "상품 분석",
                subtitle = "analysis",
                imageRes = R.drawable.jellbbo_anal,
                actions = listOf(
                    CarouselAction("매칭률", "service_matching"),
                    CarouselAction("성분 분석", "service_ingredient")
                )
            ),
            CarouselCard(
                title = "커뮤니티",
                subtitle = "community",
                imageRes = R.drawable.jellbbo_comm,
                actions = listOf(
                    CarouselAction("그룹채팅", "service_community_groupchat"),
                    CarouselAction("조합왕",   "service_community_combo")
                )
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { cards.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(resetKey) {
        pagerState.scrollToPage(0)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val card = cards[page]
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // 상단 헤더 영역 (이미지 + 타이틀)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        // 1) 이미지가 헤더를 꽉 채우도록
                        Image(
                            painter = painterResource(card.imageRes),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = card.subtitle,
                                color = Color(0xFF4A4A4A),
                                fontFamily = Pretendard,
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = card.title,
                                color = Color(0xFF4A4A4A),
                                fontFamily = Pretendard,
                                fontWeight = FontWeight(600),
                                fontSize = 20.sp
                            )
                        }
                    }


                    // 구분선 느낌의 여백
                    Spacer(Modifier.height(6.dp))

                    // 액션 2개(세로 리스트 버튼 느낌)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        card.actions.forEach { a ->
                            ChatChoiceChip( // 기존 세로형 칩 재사용
                                label = a.label,
                                onClick = { onNext(a.next) }
                            )
                        }
                    }
                }
            }
        }

        // 좌/우 네비게이션 원형 버튼
        if (pagerState.canScrollBackward) {
            IconButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-12).dp)
                    .size(36.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_back),
                    contentDescription = "이전",
                    tint = Color(0xFF424242),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (pagerState.canScrollForward) {
            IconButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 12.dp)
                    .size(36.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_back),
                    contentDescription = "다음",
                    tint = Color(0xFF424242),
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(180f)
                )
            }
        }
    }
}
