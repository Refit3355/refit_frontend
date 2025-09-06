package com.refit.app.ui.screen

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.R
import com.refit.app.data.health.modelAndView.HealthViewModel
import com.refit.app.data.product.modelAndView.RecommendationViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.ChartHeader
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.health.chart.SleepChart
import com.refit.app.ui.composable.health.chart.SleepCompareAvgChart
import com.refit.app.ui.composable.health.chart.SleepCompareRecChart
import com.refit.app.ui.composable.home.HomeProductRow
import com.refit.app.ui.composable.home.SectionHeader
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SleepDetailScreen(
    navController: NavController,
    vm: HealthViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val rows = uiState.rows
    val ctx = LocalContext.current

    val recommendVm: RecommendationViewModel = viewModel()
    val recommendState by recommendVm.state.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.permissionGranted) vm.fetch(ctx)
        else vm.onPermissionGranted(ctx)
        recommendVm.loadRecommendations(type = 0, limit = 100)
    }

    if (rows.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("수면 데이터가 아직 없어요.", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard))
        }
        return
    }

    val yesterdaySleep = rows.lastOrNull()?.sleepMinutes ?: 0
    val koreanAvgSleep = 387    // 6시간 27분
    val recommendedSleep = 480  // 8시간

    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val xLabels = days.map {
        if (it == today) "오늘"
        else it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    }

    val recentRows = rows.takeLast(7)
    val sortedData = days.zip(recentRows)
    val sleepEntries = sortedData.mapIndexed { idx, pair ->
        BarEntry(idx.toFloat(), (pair.second.sleepMinutes ?: 0).toFloat())
    }

    val indexFormatter = IndexAxisValueFormatter(xLabels)
    val pretendardBold: Typeface? = ResourcesCompat.getFont(ctx, R.font.pretendard_bold)

    var chart1Visible by remember { mutableStateOf(false) }
    var chart2Visible by remember { mutableStateOf(false) }
    var chart3Visible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val imageLoader = ImageLoader.Builder(ctx)
            .components {
                add(GifDecoder.Factory())
                add(ImageDecoderDecoder.Factory())
            }
            .build()

        val nickname = UserPrefs.getNickname() ?: "사용자"

        // ---------------- GIF 카드 ----------------
        GifCard(
            nickname = nickname,
            imageLoader = imageLoader,
            gifRes = R.raw.sleeping_jellbbo,
            message = buildAnnotatedString {
                append("${nickname}님 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("재충전을 위해서\n")
                }
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("잠시 쉬어가도 ")
                }
                append("괜찮아요.")
            }
        )

        Spacer(Modifier.height(20.dp))

        // ---------------- 1번째 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("최근 7일 동안의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("수면 기록")
                }
                append("을 확인했어요.\n충분한 수면은 피부 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("재생")
                }
                append("과 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("면역력")
                }
                append(" 유지에 도움을 줍니다.")
            }
        )
        SleepChart(
            sleepEntries = sleepEntries,
            koreanAvgSleep = koreanAvgSleep,
            pretendardBold = pretendardBold,
            indexFormatter = indexFormatter,
            chartVisible = chart1Visible
        ) { chart1Visible = true }

        Spacer(Modifier.height(32.dp))

        // ---------------- 2번째 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("오늘 나의 수면 시간과 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("일반인 평균 시간")
                }
                append("을 비교해 보았어요.")
            }
        )
        SleepCompareAvgChart(
            yesterdaySleep = yesterdaySleep,
            koreanAvgSleep = koreanAvgSleep,
            chartVisible = chart2Visible
        ) { chart2Visible = true }

        Spacer(Modifier.height(32.dp))

        // ---------------- 3번째 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("오늘 나의 수면 시간과 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("권장 수면 시간")
                }
                append("을 비교해 보았어요.")
            }
        )
        SleepCompareRecChart(
            yesterdaySleep = yesterdaySleep,
            recommendedSleep = recommendedSleep,
            chartVisible = chart3Visible
        ) { chart3Visible = true }

        Spacer(Modifier.height(32.dp))

        // ---------------- 추천 상품 섹션 ----------------
        val recommendMsg = buildAnnotatedString {
            append(nickname)
            append("님을 위한 ")
            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                append("맞춤형 상품들 보러가기")
            }
        }

        SectionHeader(
            title = recommendMsg,
            onMore = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "recommendation_items",
                    recommendState.items
                )
                navController.navigate("recommendation/1")
            }
        )

        HomeProductRow(
            products = recommendState.items.take(10),
            onClick = { p -> navController.navigate("product/${p.id}") }
        )
    }
}
