package com.refit.app.ui.screen

import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.refit.app.R
import com.refit.app.data.health.HealthRepo
import com.refit.app.data.health.modelAndView.HealthViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.ChartHeader
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.util.health.ChartUtils
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.HealthConnectClient
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StepsDetailScreen(
    navController: NavHostController,
    vm: HealthViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val rows = uiState.rows
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        val allGranted = HealthRepo.readPerms.all { granted.contains(it) }
        if (allGranted) {
            vm.onPermissionGranted(context)
            vm.onDaysChanged(context, 7)
        }
    }

    LaunchedEffect(Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (HealthRepo.readPerms.all { granted.contains(it) }) {
            vm.onPermissionGranted(context)
            vm.onDaysChanged(context, 7)
        } else {
            permissionLauncher.launch(HealthRepo.readPerms)
        }
    }

    val navyBlue = Color(red = 30, green = 60, blue = 114)
    val wineRed = Color(red = 150, green = 50, blue = 90)

    val pretendardBold: Typeface? = ResourcesCompat.getFont(context, R.font.pretendard_bold)

    // 일반인 평균 기준 값
    val koreanAvgSteps = 9611f
    val kcalAvg = 2350f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val imageLoader = ImageLoader.Builder(context)
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
            gifRes = R.raw.walking_jellbbo,
            message = buildAnnotatedString {
                append("${nickname}님이 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("한 주 동안")
                }
                append("\n")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("걸어서")
                }
                append(" 해낸 결과에요!")
            }
        )

        if (rows.isEmpty()) {
            Text(
                "데이터가 없습니다.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
            return
        }

        val todaySteps = rows.lastOrNull()?.steps ?: 0L
        val todayKcal = rows.lastOrNull()?.totalKcal ?: 0.0

        val today = LocalDate.now()
        val days = (0..6).map { today.minusDays(it.toLong()) }.reversed()
        val xLabels = days.map {
            if (it == today) "오늘"
            else it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        }

        val sortedData = days.zip(rows.takeLast(7))
        val stepEntries = sortedData.mapIndexed { idx, pair -> BarEntry(idx.toFloat(), (pair.second.steps ?: 0L).toFloat()) }
        val kcalEntries = sortedData.mapIndexed { idx, pair -> BarEntry(idx.toFloat(), (pair.second.totalKcal ?: 0.0).toFloat()) }

        val maxSteps = stepEntries.maxOfOrNull { it.y } ?: 0f
        val maxKcal = kcalEntries.maxOfOrNull { it.y } ?: 0f

        val indexFormatter = IndexAxisValueFormatter(xLabels)

        var chart1Visible by remember { mutableStateOf(false) }
        var chart2Visible by remember { mutableStateOf(false) }
        var chart3Visible by remember { mutableStateOf(false) }
        var chart4Visible by remember { mutableStateOf(false) }

        Spacer(Modifier.height(12.dp))

        // ---------------- 1번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("지난 일주일 동안의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("걸음수") }
                append("를 확인했어요.\n")
                append("걷기는 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈액순환") }
                append("을 촉진해 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 건강") }
                append("을 유지하는 데 도움을 줍니다.")
            }
        )
        ChartBox(
            height = 220.dp,
            visible = chart1Visible,
            onVisible = { chart1Visible = true }
        ) { ctx ->
            com.github.mikephil.charting.charts.BarChart(ctx).apply {
                val barDataSet = BarDataSet(stepEntries, "").apply {
                    setDrawValues(true)
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(12f)
                    colors = stepEntries.mapIndexed { idx, _ ->
                        if (idx == stepEntries.size - 1) MainPurple.toArgb() else Color.LightGray.toArgb()
                    }
                    valueFormatter = object : ValueFormatter() {
                        override fun getBarLabel(barEntry: BarEntry?) =
                            "${barEntry?.y?.toInt()}"
                    }
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
                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(koreanAvgSteps, "일반인 평균 ${koreanAvgSteps.toInt()} 걸음", pretendardBold)
                    )
                    val maxVal = maxOf(maxSteps, koreanAvgSteps)
                    axisMinimum = 0f
                    axisMaximum = (maxVal * 1.1f)
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    setTextColor(Color.DarkGray.toArgb())
                    valueFormatter = indexFormatter
                }
                if (chart1Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 2번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("일주일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("칼로리 소모량") }
                append("을 보여드릴게요!\n")
                append("꾸준한 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("에너지 소모") }
                append("는 체중 관리뿐만 아니라 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 노화 억제") }
                append("에도 긍정적입니다.")
            }
        )
        ChartBox(
            height = 220.dp,
            visible = chart2Visible,
            onVisible = { chart2Visible = true }
        ) { ctx ->
            com.github.mikephil.charting.charts.BarChart(ctx).apply {
                val barDataSet = BarDataSet(kcalEntries, "").apply {
                    setDrawValues(true)
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(12f)
                    colors = kcalEntries.mapIndexed { idx, _ ->
                        if (idx == kcalEntries.size - 1) MainPurple.toArgb() else Color.LightGray.toArgb()
                    }
                    valueFormatter = object : ValueFormatter() {
                        override fun getBarLabel(barEntry: BarEntry?) =
                            "${barEntry?.y?.toInt()}"
                    }
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
                    val maxVal = maxOf(maxKcal, kcalAvg)
                    axisMinimum = 0f
                    axisMaximum = (maxVal * 1.1f)
                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(kcalAvg, "일반인 평균 ${kcalAvg.toInt()} kcal", pretendardBold)
                    )
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    setTextColor(Color.DarkGray.toArgb())
                    valueFormatter = indexFormatter
                }
                if (chart2Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 3번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("오늘의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("걸음수") }
                append("를 일반인 평균값과 비교해 보았어요.\n")
                if (todaySteps >= koreanAvgSteps) {
                    append("평균보다 많이 걸으면 피부 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈류 개선") }
                    append("에 도움이 됩니다.")
                } else {
                    append("평균보다 적게 걸으면 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 활력") }
                    append("이 줄어들 수 있어요.")
                }
            }
        )
        ChartBox(
            height = 200.dp,
            visible = chart3Visible,
            onVisible = { chart3Visible = true }
        ) { ctx ->
            com.github.mikephil.charting.charts.BarChart(ctx).apply {
                val my = BarEntry(0f, todaySteps.toFloat())
                val avg = BarEntry(1f, koreanAvgSteps)
                val mySet = BarDataSet(listOf(my), "나").apply {
                    color = MainPurple.toArgb()
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                }
                val avgSet = BarDataSet(listOf(avg), "한국인 평균").apply {
                    color = Color.LightGray.toArgb()
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
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
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    val maxVal = maxOf(todaySteps.toFloat(), koreanAvgSteps)
                    axisMinimum = 0f
                    axisMaximum = (maxVal * 1.1f)
                }
                xAxis.apply {
                    isEnabled = false
                    setDrawGridLines(false)
                }
                legend.isEnabled = true
                if (chart3Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 4번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("오늘의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("칼로리 소모") }
                append("를 성별 평균과 비교해 보았어요.\n")
                if (todayKcal >= kcalAvg) {
                    append("평균보다 많은 칼로리 소모는 피부 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("노화 억제") }
                    append("와 체지방 관리에 긍정적입니다.")
                } else {
                    append("평균보다 적은 칼로리 소모는 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 활력") }
                    append("이 떨어질 수 있어요.")
                }
            }
        )
        ChartBox(
            height = 200.dp,
            visible = chart4Visible,
            onVisible = { chart4Visible = true }
        ) { ctx ->
            com.github.mikephil.charting.charts.BarChart(ctx).apply {
                val my = BarEntry(0f, todayKcal.toFloat())
                val male = BarEntry(1f, 2600f)
                val female = BarEntry(2f, 2100f)
                val mySet = BarDataSet(listOf(my), "나").apply {
                    color = MainPurple.toArgb()
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                }
                val maleSet = BarDataSet(listOf(male), "남자 평균").apply {
                    color = navyBlue.toArgb()
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                }
                val femaleSet = BarDataSet(listOf(female), "여자 평균").apply {
                    color = wineRed.toArgb()
                    setValueTextColor(Color.DarkGray.toArgb())
                    setValueTextSize(10f)
                    setDrawValues(true)
                }
                val groupSpace = 0.3f
                val barSpace = 0.05f
                val barWidth = 0.2f
                data = BarData(mySet, maleSet, femaleSet).apply { this.barWidth = barWidth }
                val groupWidth = data.getGroupWidth(groupSpace, barSpace)
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = groupWidth
                groupBars(0f, groupSpace, barSpace)
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    val maxVal = maxOf(todayKcal.toFloat(), 2600f, 2100f, kcalAvg)
                    axisMinimum = 0f
                    axisMaximum = (maxVal * 1.1f)
                }
                xAxis.apply {
                    isEnabled = false
                    setDrawGridLines(false)
                }
                legend.isEnabled = true
                if (chart4Visible) {
                    animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                }
            }
        }
    }
}
