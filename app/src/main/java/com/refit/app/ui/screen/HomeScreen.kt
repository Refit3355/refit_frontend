package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import com.refit.app.R
import com.refit.app.ui.composable.home.GreetingCard
import com.refit.app.ui.composable.home.HomeProductRow
import com.refit.app.ui.composable.home.MetricRow
import com.refit.app.ui.composable.home.SectionHeader
import com.refit.app.data.health.model.MetricItem
import com.refit.app.ui.fake.MemberDummy
import com.refit.app.data.home.modelAndView.HomeViewModel
import com.refit.app.util.home.getWeatherIcon
import com.refit.app.util.home.highlightText

@Composable
fun HomeScreen(
    navController: NavHostController,
    vm: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scroll = rememberScrollState()
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.loadData(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scroll)
            .padding(bottom = 16.dp)
    ) {
        // ===== 나만의 정보 섹션 =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE9E4F2))
                .padding(top = 36.dp)
        ) {
            GreetingCard(
                nickname = MemberDummy.member.nickname,
                tags = listOf(
                    "#${MemberDummy.member.concern1}",
                    "#${MemberDummy.member.concern2}",
                    "#${MemberDummy.member.concern3}"
                )
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

            Spacer(Modifier.height(16.dp))

            // === 수면시간 기반 섹션 ===
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

            Spacer(Modifier.height(16.dp))

            // === 날씨 기반 섹션 ===
            SectionHeader(
                title = highlightText("매일 달라지는 날씨에 맞춘 헤어 솔루션", listOf("날씨", "헤어 솔루션")),
                onMore = {
                    navController.navigate("recommendation/2")
                }
            )
            HomeProductRow(
                products = uiState.weatherProducts,
                onClick = { p -> navController.navigate("product/${p.id}") }
            )

            Spacer(Modifier.height(16.dp))

            // === 생활 리듬 기반 섹션 ===
            SectionHeader(
                title = highlightText("당신의 생활 리듬에 맞춘 건강 케어", listOf("생활 리듬", "건강 케어")),
                onMore = {
                    navController.navigate("recommendation/3")
                }
            )
            HomeProductRow(
                products = uiState.rhythmProducts,
                onClick = { p -> navController.navigate("product/${p.id}") }
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
