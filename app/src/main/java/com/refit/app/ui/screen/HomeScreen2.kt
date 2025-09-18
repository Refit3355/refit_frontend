package com.refit.app.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.refit.app.R
import com.refit.app.ui.composable.home.HomeProductRow
import com.refit.app.ui.composable.home.SectionHeader
import com.refit.app.data.home.modelAndView.HomeViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.home.version2.MyInfoCarouselV2
import com.refit.app.ui.composable.product.floating.SpeedDialButton
import com.refit.app.ui.composable.product.floating.SpeedDialItem
import com.refit.app.ui.composable.product.floating.SpeedDialMenu
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.util.common.TypingText
import com.refit.app.util.home.highlightText

@SuppressLint("ResourceType")
@Composable
fun HomeScreen2(
    navController: NavHostController,
    vm: HomeViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val nickname = UserPrefs.getNickname()
    val allTags = UserPrefs.getTags()
    val sleepMinutes = uiState.sleepMinutes

    var isFabMenuOpen by remember { mutableStateOf(false) }

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
    // 홈 진입/이탈에 따라 polling 시작/중단
    LaunchedEffect(currentRoute) {
        if (currentRoute == "home") {
            vm.loadData(context)
            vm.startHealthPolling(context)
        } else {
            vm.stopHealthPolling()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ===== 스크롤 본문 =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            Spacer(Modifier.height(16.dp))

            // ===== 나만의 정보 섹션 =====
            MyInfoCarouselV2(
                nickname = nickname ?: "사용자",
                steps = uiState.steps ?: 0,
                sleepMinutes = uiState.sleepMinutes ?: 0,
                tempC = uiState.temperature?.toInt() ?: 24,
                onStepsClick = { navController.navigate("stepsDetail") },
                onSleepClick = { navController.navigate("sleepDetail") },
                onWeatherClick = { navController.navigate("weatherDetail") }
            )

            Spacer(Modifier.height(16.dp))

            MaterialTheme(
                typography = MaterialTheme.typography.copy(
                    titleMedium = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Black,
                        fontFamily = Pretendard
                    ),
                    bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontFamily = Pretendard
                    ),
                    bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontFamily = Pretendard
                    ),
                    labelMedium = MaterialTheme.typography.labelMedium.copy(
                        color = Color.Gray,
                        fontFamily = Pretendard
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    // === 걸음수 기반 섹션 ===
                    run {
                        val stepsMsg = if ((uiState.steps ?: 0) > 5000) {
                            highlightText(
                                "활동적인 하루, 컨디션 회복이 필요합니다",
                                listOf("활동적인 하루", "컨디션", "회복")
                            )
                        } else {
                            highlightText(
                                "움직임이 적을수록 활력을 채워주세요",
                                listOf("움직임", "활력")
                            )
                        }

                        SectionHeader(title = stepsMsg, onMore = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "recommendation_items",
                                uiState.stepProducts
                            )
                            navController.navigate("recommendation/0")
                        })
                        if (uiState.isLoadingStep) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = R.raw.ic_loading_analysis,
                                    contentDescription = "Loading Mascot",
                                    imageLoader = imageLoader,
                                    modifier = Modifier.size(80.dp),
                                    alignment = Alignment.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                TypingText("로딩중...")
                            }
                        } else {
                            HomeProductRow(
                                products = uiState.stepProducts.take(10),
                                onClick = { p -> navController.navigate("product/${p.id}") }
                            )
                        }
                    }

                    // === 수면시간 기반 섹션 ===
                    run {
                        val sleepMsg = if ((uiState.sleepMinutes ?: 0) > 420) {
                            highlightText("밤새 회복한 피부에 더해주는 촉촉한 케어", listOf("밤새 회복한 피부", "촉촉한 케어"))
                        } else {
                            highlightText("짧은 수면, 지친 피부를 위한 에너지 충전", listOf("짧은 수면", "에너지 충전"))
                        }
                        SectionHeader(title = sleepMsg, onMore = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "recommendation_items",
                                uiState.sleepProducts
                            )
                            navController.navigate("recommendation/1")
                        })
                        if (uiState.isLoadingSleep) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = R.raw.ic_loading_analysis,
                                    contentDescription = "Loading Mascot",
                                    imageLoader = imageLoader,
                                    modifier = Modifier.size(80.dp),
                                    alignment = Alignment.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                TypingText("로딩중...")
                            }
                        } else {
                            HomeProductRow(
                                products = uiState.sleepProducts.take(10),
                                onClick = { p -> navController.navigate("product/${p.id}") }
                            )
                        }
                    }

                    // === 날씨 기반 섹션 ===
                    SectionHeader(
                        title = highlightText("매일 달라지는 날씨에 맞춘 헤어 솔루션", listOf("날씨", "헤어 솔루션")),
                        onMore = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "recommendation_items",
                                uiState.weatherProducts
                            )
                            navController.navigate("recommendation/2")
                        }
                    )
                    if (uiState.isLoadingWeather) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = R.raw.ic_loading_analysis,
                                contentDescription = "Loading Mascot",
                                imageLoader = imageLoader,
                                modifier = Modifier.size(80.dp),
                                alignment = Alignment.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            TypingText("로딩중...")
                        }
                    } else {
                        HomeProductRow(
                            products = uiState.weatherProducts.take(10),
                            onClick = { p -> navController.navigate("product/${p.id}") }
                        )
                    }

                    // === 생활 리듬 기반 섹션 ===
                    SectionHeader(
                        title = highlightText("당신의 생활 리듬에 맞춘 건강 케어", listOf("생활 리듬", "건강 케어")),
                        onMore = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "recommendation_items",
                                uiState.rhythmProducts
                            )
                            navController.navigate("recommendation/3")
                        }
                    )
                    if (uiState.isLoadingRhythm) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = R.raw.ic_loading_analysis,
                                contentDescription = "Loading Mascot",
                                imageLoader = imageLoader,
                                modifier = Modifier.size(80.dp),
                                alignment = Alignment.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            TypingText("로딩중...")
                        }
                    } else {
                        HomeProductRow(
                            products = uiState.rhythmProducts.take(10),
                            onClick = { p -> navController.navigate("product/${p.id}") }
                        )
                    }
                }
            }

                Spacer(Modifier.height(16.dp))
            }

            // FAB 뒤로 펼쳐지는 메뉴 (오버레이)
            SpeedDialMenu(
                isOpen = isFabMenuOpen,
                onDismiss = { isFabMenuOpen = false },
                items = listOf(
                    SpeedDialItem(
                        label = "성분 분석",
                        iconRes = R.drawable.ic_floating_search
                    ) { navController.navigate("ingredient") },
                    SpeedDialItem(
                        label = "챗봇 연결",
                        iconRes = R.drawable.ic_floating_bot
                    ) { navController.navigate("chatbot") },
                ),
                endPadding = 16.dp,
                bottomPaddingFromFab = 96.dp,
                modifier = Modifier.zIndex(1f)
            )

            // FAB 토글 버튼 (오른쪽 하단)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .zIndex(2f),
                contentAlignment = Alignment.BottomEnd
            ) {
                SpeedDialButton(
                    isOpen = isFabMenuOpen,
                    onToggle = { isFabMenuOpen = !isFabMenuOpen },
                    iconRes = R.drawable.ic_jellbbo_chatbot_floating,
                    openBgColor = Color.White,
                    closeIconTint = MainPurple
                )
            }
    }
}
