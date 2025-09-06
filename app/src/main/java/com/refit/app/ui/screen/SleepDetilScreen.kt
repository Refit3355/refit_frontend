package com.refit.app.ui.screen

import android.graphics.Color
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.refit.app.R
import com.refit.app.data.health.modelAndView.HealthViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.LightPurple
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
            Text(
                "수면 데이터가 아직 없어요.",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard)
            )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ---------------- GIF + 닉네임 카드 ----------------
        val imageLoader = ImageLoader.Builder(ctx)
            .components {
                add(GifDecoder.Factory())
                add(ImageDecoderDecoder.Factory())
            }
            .build()

        val nickname = UserPrefs.getNickname()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightPurple, shape = RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = R.raw.walking_jellbbo, // 수면 전용 GIF 리소스로 교체 가능
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        buildAnnotatedString {
                            append("${nickname}님의 ")
                            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                                append("수면 리포트")
                            }
                            append("예요!")
                        },
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------------- 1번째 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_sleep,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = ctx.resources.displayMetrics.heightPixels
                    if (!chart1Visible && y in 0f..screenHeight.toFloat()) {
                        chart1Visible = true
                    }
                }
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = chart1Visible,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(700))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        com.github.mikephil.charting.charts.BarChart(context).apply {
                            val barDataSet = BarDataSet(sleepEntries, "").apply {
                                setDrawValues(true)
                                setValueTextColor(Color.DKGRAY)
                                setValueTextSize(12f)
                                valueTypeface = Pretendard // ✅ Pretendard 적용
                                colors = sleepEntries.mapIndexed { idx, _ ->
                                    if (idx == sleepEntries.size - 1) MainPurple.toArgb() else Color.LTGRAY
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
                                removeAllLimitLines()
                                addLimitLine(
                                    LimitLine(
                                        koreanAvgSleep.toFloat(),
                                        "한국인 평균 ${koreanAvgSleep/60}시간 ${koreanAvgSleep%60}분"
                                    ).apply {
                                        lineColor = MainPurple.toArgb()
                                        lineWidth = 1.5f
                                        enableDashedLine(10f, 10f, 0f)
                                        textColor = MainPurple.toArgb()
                                        textSize = 11f
                                        typeface = Pretendard
                                    }
                                )
                            }
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                granularity = 1f
                                setTextColor(Color.DKGRAY)
                                valueFormatter = indexFormatter
                            }
                            if (chart1Visible) {
                                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 2번째 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_sleep,
            text = buildAnnotatedString {
                append("어제의 수면 시간과 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("한국 평균")
                }
                append("을 비교했어요.")
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = ctx.resources.displayMetrics.heightPixels
                    if (!chart2Visible && y in 0f..screenHeight.toFloat()) {
                        chart2Visible = true
                    }
                }
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = chart2Visible,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(700))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        com.github.mikephil.charting.charts.BarChart(context).apply {
                            val my = BarEntry(0f, yesterdaySleep.toFloat())
                            val avg = BarEntry(1f, koreanAvgSleep.toFloat())
                            val mySet = BarDataSet(listOf(my), "나").apply {
                                color = MainPurple.toArgb()
                                setValueTextColor(Color.DKGRAY)
                                setValueTextSize(10f)
                                valueTypeface = Pretendard
                                setDrawValues(true)
                                valueFormatter = SleepFormatter()
                            }
                            val avgSet = BarDataSet(listOf(avg), "한국 평균").apply {
                                color = Color.LTGRAY
                                setValueTextColor(Color.DKGRAY)
                                setValueTextSize(10f)
                                valueTypeface = Pretendard
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
                            axisLeft.isEnabled = false
                            axisRight.isEnabled = false
                            xAxis.isEnabled = false
                            legend.isEnabled = true
                            if (chart2Visible) {
                                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 3번째 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_sleep,
            text = buildAnnotatedString {
                append("어제의 수면 시간과 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("권장 수면")
                }
                append("을 비교했어요.")
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = ctx.resources.displayMetrics.heightPixels
                    if (!chart3Visible && y in 0f..screenHeight.toFloat()) {
                        chart3Visible = true
                    }
                }
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = chart3Visible,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(700))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        com.github.mikephil.charting.charts.BarChart(context).apply {
                            val my = BarEntry(0f, yesterdaySleep.toFloat())
                            val rec = BarEntry(1f, recommendedSleep.toFloat())
                            val mySet = BarDataSet(listOf(my), "나").apply {
                                color = MainPurple.toArgb()
                                setValueTextColor(Color.DKGRAY)
                                setValueTextSize(10f)
                                valueTypeface = Pretendard
                                setDrawValues(true)
                                valueFormatter = SleepFormatter()
                            }
                            val recSet = BarDataSet(listOf(rec), "권장 수면").apply {
                                color = Color.LTGRAY
                                setValueTextColor(Color.DKGRAY)
                                setValueTextSize(10f)
                                valueTypeface = Pretendard
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
                            axisLeft.isEnabled = false
                            axisRight.isEnabled = false
                            xAxis.isEnabled = false
                            legend.isEnabled = true
                            if (chart3Visible) {
                                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                            }
                        }
                    }
                )
            }
        }
    }
}

class SleepFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val h = value.toInt() / 60
        val m = value.toInt() % 60
        return "${h}h ${m}m"
    }
}
