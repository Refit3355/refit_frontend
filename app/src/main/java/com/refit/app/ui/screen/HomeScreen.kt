package com.refit.app.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.refit.app.R
import com.refit.app.data.auth.model.HealthInfoDto
import com.refit.app.ui.composable.home.GreetingCard
import com.refit.app.ui.composable.home.HomeProductRow
import com.refit.app.ui.composable.home.MetricRow
import com.refit.app.ui.composable.home.SectionHeader
import com.refit.app.data.health.model.MetricItem
import com.refit.app.ui.fake.MemberDummy
import com.refit.app.data.home.modelAndView.HomeViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.util.home.getWeatherIcon
import com.refit.app.util.home.highlightText

@Composable
fun HomeScreen(
    navController: NavHostController,
    vm: HomeViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    val nickname = UserPrefs.getNickname()
    val health = UserPrefs.getHealth()

    val allTags = health?.toTags().orEmpty()
    val selectedTags = allTags.shuffled().take(3).sorted()

    LaunchedEffect(Unit) {
        vm.loadData(context)
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
                .padding(bottom = 100.dp) // FAB와 겹치지 않도록 여백
        ) {
            // ===== 나만의 정보 섹션 =====
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE9E4F2))
                    .padding(top = 36.dp)
            ) {
                GreetingCard(
                    nickname = nickname ?: "사용자",
                    tags = selectedTags
                )

                Spacer(Modifier.height(12.dp))

                MetricRow(
                    items = listOf(
                        MetricItem(
                            "오늘의 걸음수",
                            uiState.steps?.takeIf { it > 0 }?.toString() ?: "--",
                            "보",
                            R.drawable.jellbbo_walk,
                            iconOffsetY = -20,
                            onClick = { navController.navigate("stepsDetail") }
                        ),
                        MetricItem(
                            "어제의 수면시간",
                            uiState.sleepMinutes?.takeIf { it > 0 }?.let { vm.formatSleep(it) } ?: "--",
                            "시간",
                            R.drawable.jellbbo_sleep,
                            iconOffsetY = -22,
                            onClick = { navController.navigate("sleepDetail") }
                        ),
                        MetricItem(
                            "현재의 기온",
                            uiState.temperature?.toInt()?.toString() ?: "--",
                            "℃",
                            getWeatherIcon(uiState.temperature, uiState.weatherCode),
                            iconOffsetY = -25,
                            onClick = { navController.navigate("weatherDetail") }
                        )
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            MaterialTheme(
                typography = MaterialTheme.typography.copy(
                    titleMedium = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
                    bodyMedium  = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    bodyLarge   = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    labelMedium = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                )
            ) {
                // === 걸음수 기반 섹션 ===
                run {
                    val stepsMsg = if ((uiState.steps ?: 0) > 5000) {
                        highlightText("활동적인 하루, 피부도 휴식이 필요합니다", listOf("활동적인 하루", "휴식"))
                    } else {
                        highlightText("움직임이 적을수록 필요한 피부 활력 충전", listOf("움직임", "활력 충전"))
                    }
                    SectionHeader(title = stepsMsg, onMore = {
                        navController.navigate("recommendation/0")
                    })
                    HomeProductRow(
                        products = uiState.stepProducts,
                        onClick = { p -> navController.navigate("product/${p.id}") }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // === 수면시간 기반 섹션 ===
                run {
                    val sleepMsg = if ((uiState.sleepMinutes ?: 0) > 420) {
                        highlightText("밤새 회복한 피부에 더해주는 촉촉한 케어", listOf("밤새 회복한 피부", "촉촉한 케어"))
                    } else {
                        highlightText("짧은 수면, 지친 피부를 위한 에너지 충전", listOf("짧은 수면", "에너지 충전"))
                    }
                    SectionHeader(title = sleepMsg, onMore = {
                        navController.navigate("recommendation/1")
                    })
                    HomeProductRow(
                        products = uiState.sleepProducts,
                        onClick = { p -> navController.navigate("product/${p.id}") }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // === 날씨 기반 섹션 ===
                SectionHeader(
                    title = highlightText("매일 달라지는 날씨에 맞춘 헤어 솔루션", listOf("날씨", "헤어 솔루션")),
                    onMore = { navController.navigate("recommendation/2") }
                )
                HomeProductRow(
                    products = uiState.weatherProducts,
                    onClick = { p -> navController.navigate("product/${p.id}") }
                )

                Spacer(Modifier.height(16.dp))

                // === 생활 리듬 기반 섹션 ===
                SectionHeader(
                    title = highlightText("당신의 생활 리듬에 맞춘 건강 케어", listOf("생활 리듬", "건강 케어")),
                    onMore = { navController.navigate("recommendation/3") }
                )
                HomeProductRow(
                    products = uiState.rhythmProducts,
                    onClick = { p -> navController.navigate("product/${p.id}") }
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // ===== 커스텀 텍스트 FAB =====
        FloatingActionButton(
            onClick = { navController.navigate("ingredient") },
            shape = CircleShape,
            containerColor = MainPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(60.dp)
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

fun HealthInfoDto.toTags(): List<String> {
    val tags = mutableListOf<String>()
    if (eyeHealth > 0) tags.add("#눈 건강")
    if (fatigue > 0) tags.add("#피로 회복")
    if (sleepStress > 0) tags.add("#수면/스트레스")
    if (immuneCare > 0) tags.add("#면역 케어")
    if (muscleHealth > 0) tags.add("#근육 건강")
    if (gutHealth > 0) tags.add("#장 건강")
    if (bloodCirculation > 0) tags.add("#혈액 순환")
    return tags
}


