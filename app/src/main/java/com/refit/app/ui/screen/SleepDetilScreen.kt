package com.refit.app.ui.screen

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.refit.app.ui.composable.health.chartHeader.TitleLine
import com.refit.app.ui.composable.health.chartHeader.InfoCallout
import androidx.compose.ui.text.AnnotatedString

private fun minToHM(min: Int): String = "${min/60}h ${min%60}m"

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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
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
                append("${nickname}님, 잘하고 계세요.\n오늘 밤은 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("10분만 더 일찍") }
                append(" 누워볼까요?")
            }
        )

        Spacer(Modifier.height(30.dp))

        // 본문 구역만 좌우 여백
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            // 1) 주간 수면 시간
            TitleLine(iconRes = R.drawable.ic_sleep, title = "주간 수면 시간")
            Spacer(Modifier.height(8.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("지난 7일의 수면 패턴을 정리했어요. ")
                    append("짧더라도 일정한 시간에 잠들고 일어나는 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("규칙 수면") }
                    append("이 컨디션 유지에 도움이 됩니다.")
                }
            )
            Spacer(Modifier.height(10.dp))

            // 차트: 리미트라인 라벨은 내부에서 숨김 처리
            SleepChart(
                sleepEntries = sleepEntries,
                koreanAvgSleep = koreanAvgSleep,
                pretendardBold = pretendardBold,
                indexFormatter = indexFormatter,
                chartVisible = chart1Visible
            ) { chart1Visible = true }

            Spacer(Modifier.height(80.dp))

            // 2) 어제 vs 한국 평균
            TitleLine(iconRes = R.drawable.ic_compare, title = "어제 수면 vs 평균")
            Spacer(Modifier.height(8.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("어제 수면을 한국인 평균과 비교했어요. ")
                    append("부담 없는 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("취침 루틴") }
                    append("을 만들어 보세요.")
                }
            )
            Spacer(Modifier.height(10.dp))
            SleepCompareAvgChart(
                yesterdaySleep = yesterdaySleep,
                koreanAvgSleep = koreanAvgSleep,
                chartVisible = chart2Visible
            ) { chart2Visible = true }

            Spacer(Modifier.height(80.dp))

            // 3) 어제 vs 권장 수면
            TitleLine(iconRes = R.drawable.ic_compare, title = "어제 수면 vs 권장")
            Spacer(Modifier.height(8.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("권장 수면 시간과 비교해 현재 위치를 알려드려요. ")
                    append("오늘은 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("잠들기 전 10분") }
                    append("만 더 일찍 눕는 건 어떨까요?")
                }
            )
            Spacer(Modifier.height(10.dp))
            SleepCompareRecChart(
                yesterdaySleep = yesterdaySleep,
                recommendedSleep = recommendedSleep,
                chartVisible = chart3Visible
            ) { chart3Visible = true }

            Spacer(Modifier.height(62.dp))

            // ---------------- 추천 상품 섹션 ----------------
            val recommendMsg = buildAnnotatedString {
                append(nickname)
                append("님을 위한 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("수면 맞춤 상품")
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

            Spacer(Modifier.height(32.dp))
        }
    }
}
