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
import androidx.compose.ui.graphics.toArgb
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.R
import com.refit.app.data.health.modelAndView.HealthViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.ChartHeader
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.util.health.ChartUtils
import com.refit.app.util.health.SleepFormatter
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

    LaunchedEffect(Unit) {
        if (uiState.permissionGranted) vm.fetch(ctx)
        else vm.onPermissionGranted(ctx)
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

    var chart1Visible by remember { mutableStateOf(false) }
    var chart2Visible by remember { mutableStateOf(false) }
    var chart3Visible by remember { mutableStateOf(false) }

    val pretendardBold: Typeface? = ResourcesCompat.getFont(ctx, R.font.pretendard_bold)

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
        ChartBox(
            height = 220.dp,
            visible = chart1Visible,
            onVisible = { chart1Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val barDataSet = BarDataSet(sleepEntries, "").apply {
                    setDrawValues(true)
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    setValueTextSize(12f)
                    colors = sleepEntries.mapIndexed { idx, _ ->
                        if (idx == sleepEntries.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    }
                    valueFormatter = SleepFormatter()
                }
                data = BarData(barDataSet).apply { barWidth = 0.4f }
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = true
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    axisMinimum = 0f

                    val maxVal = maxOf(
                        sleepEntries.maxOfOrNull { it.y } ?: 0f,
                        koreanAvgSleep.toFloat()
                    )
                    axisMaximum = maxVal * 1.1f

                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(
                            koreanAvgSleep.toFloat(),
                            "일반인 평균 ${koreanAvgSleep / 60}h ${koreanAvgSleep % 60}m",
                            pretendardBold
                        )
                    )
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    setTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    valueFormatter = indexFormatter
                }
                if (chart1Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }

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
        ChartBox(
            height = 200.dp,
            visible = chart2Visible,
            onVisible = { chart2Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val my = BarEntry(0f, yesterdaySleep.toFloat())
                val avg = BarEntry(1f, koreanAvgSleep.toFloat())
                val mySet = BarDataSet(listOf(my), "나").apply {
                    color = MainPurple.toArgb()
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                    valueFormatter = SleepFormatter()
                }
                val avgSet = BarDataSet(listOf(avg), "한국 평균").apply {
                    color = androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                    valueFormatter = SleepFormatter()
                }
                val groupSpace = 0.4f
                val barSpace = 0.05f
                val barWidth = 0.2f
                data = BarData(mySet, avgSet).apply { this.barWidth = barWidth }
                val groupWidth = data.getGroupWidth(groupSpace, barSpace)
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = groupWidth
                groupBars(0f, groupSpace, barSpace)
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    axisMinimum = 0f
                    val maxVal = maxOf(yesterdaySleep.toFloat(), koreanAvgSleep.toFloat())
                    axisMaximum = maxVal * 1.1f
                }
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                legend.isEnabled = true
                if (chart2Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }

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
        ChartBox(
            height = 200.dp,
            visible = chart3Visible,
            onVisible = { chart3Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val my = BarEntry(0f, yesterdaySleep.toFloat())
                val rec = BarEntry(1f, recommendedSleep.toFloat())
                val mySet = BarDataSet(listOf(my), "나").apply {
                    color = MainPurple.toArgb()
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                    valueFormatter = SleepFormatter()
                }
                val recSet = BarDataSet(listOf(rec), "권장 수면").apply {
                    color = androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                    valueFormatter = SleepFormatter()
                }
                val groupSpace = 0.4f
                val barSpace = 0.05f
                val barWidth = 0.2f
                data = BarData(mySet, recSet).apply { this.barWidth = barWidth }
                val groupWidth = data.getGroupWidth(groupSpace, barSpace)
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = groupWidth
                groupBars(0f, groupSpace, barSpace)
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    axisMinimum = 0f
                    val maxVal = maxOf(yesterdaySleep.toFloat(), recommendedSleep.toFloat())
                    axisMaximum = maxVal * 1.1f
                }
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                legend.isEnabled = true
                if (chart3Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }
    }
}
