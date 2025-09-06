package com.refit.app.ui.screen

import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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
import com.refit.app.data.health.HealthRepo
import com.refit.app.data.health.modelAndView.HealthViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.HealthConnectClient
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// 말풍선 Shape 정의
class BubbleShape(private val cornerRadius: Float = 24f) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path().apply {
            // 둥근 사각형 (꼬리 공간 확보)
            addRoundRect(
                RoundRect(
                    rect = Rect(20f, 0f, size.width, size.height), // ← 왼쪽에 여백 줌
                    CornerRadius(cornerRadius, cornerRadius)
                )
            )

            // 꼬리 (왼쪽 중간)
            val tailCenterY = size.height / 2f
            moveTo(20f, tailCenterY - 20f)   // 위쪽 점
            lineTo(0f, tailCenterY)          // 왼쪽 뾰족 끝
            lineTo(20f, tailCenterY + 20f)   // 아래쪽 점
            close()
        }
        return Outline.Generic(path)
    }
}


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

    // 한국인 평균 기준 값
    val koreanAvgSteps = 9611f
    val kcalAvg = 2350f

    fun createAverageLine(value: Float, label: String): LimitLine {
        return LimitLine(value, label).apply {
            lineColor = MainPurple.toArgb()
            lineWidth = 1.5f
            enableDashedLine(10f, 10f, 0f)
            textColor = MainPurple.toArgb()
            textSize = 11f
            typeface = pretendardBold
        }
    }

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

        val nickname = UserPrefs.getNickname()

        // GIF + 닉네임 카드
        // GIF + 닉네임 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .background(LightPurple, shape = RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .offset(x = (-8).dp) // ✅ 왼쪽으로 8dp 이동 (필요시 값 조절)
            ) {
                AsyncImage(
                    model = R.raw.walking_jellbbo,
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        buildAnnotatedString {
                            append("${nickname}님이 ")
                            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                                append("한 주 동안")
                            }
                            append("\n")
                            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                                append("걸어서")
                            }
                            append(" 해낸 결과에요!")
                        },
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

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

        val indexFormatter = IndexAxisValueFormatter(xLabels)

        var chart1Visible by remember { mutableStateOf(false) }
        var chart2Visible by remember { mutableStateOf(false) }
        var chart3Visible by remember { mutableStateOf(false) }
        var chart4Visible by remember { mutableStateOf(false) }

        Spacer(Modifier.height(12.dp))

        // 1번 차트
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = context.resources.displayMetrics.heightPixels
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
                    factory = { ctx ->
                        com.github.mikephil.charting.charts.BarChart(ctx).apply {
                            val barDataSet = BarDataSet(stepEntries, "").apply {
                                setDrawValues(true)
                                setValueTextColor(android.graphics.Color.DKGRAY)
                                setValueTextSize(12f)
                                colors = stepEntries.mapIndexed { idx, _ ->
                                    if (idx == stepEntries.size - 1) MainPurple.toArgb() else android.graphics.Color.LTGRAY
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
                                addLimitLine(createAverageLine(koreanAvgSteps, "일반인 평균 ${koreanAvgSteps.toInt()} 걸음"))
                            }
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                granularity = 1f
                                setTextColor(android.graphics.Color.DKGRAY)
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

        // 2번 차트
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = context.resources.displayMetrics.heightPixels
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
                    factory = { ctx ->
                        com.github.mikephil.charting.charts.BarChart(ctx).apply {
                            val barDataSet = BarDataSet(kcalEntries, "").apply {
                                setDrawValues(true)
                                setValueTextColor(android.graphics.Color.DKGRAY)
                                setValueTextSize(12f)
                                colors = kcalEntries.mapIndexed { idx, _ ->
                                    if (idx == kcalEntries.size - 1) MainPurple.toArgb() else android.graphics.Color.LTGRAY
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
                            val maxVal = (kcalEntries.maxOfOrNull { it.y } ?: 0f).coerceAtLeast(kcalAvg)
                            axisLeft.apply {
                                isEnabled = true
                                setDrawLabels(false)
                                setDrawGridLines(false)
                                setDrawAxisLine(false)
                                axisMinimum = 0f
                                axisMaximum = maxVal + 200f
                                removeAllLimitLines()
                                addLimitLine(createAverageLine(kcalAvg, "일반인 평균 ${kcalAvg.toInt()} kcal"))
                            }
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                granularity = 1f
                                setTextColor(android.graphics.Color.DKGRAY)
                                valueFormatter = indexFormatter
                            }
                            if (chart2Visible) {
                                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // 3번 차트
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = context.resources.displayMetrics.heightPixels
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
                    factory = { ctx ->
                        com.github.mikephil.charting.charts.BarChart(ctx).apply {
                            val my = BarEntry(0f, todaySteps.toFloat())
                            val avg = BarEntry(1f, koreanAvgSteps)
                            val mySet = BarDataSet(listOf(my), "나").apply {
                                color = MainPurple.toArgb()
                                setValueTextColor(android.graphics.Color.DKGRAY)
                                setValueTextSize(10f)
                                setDrawValues(true)
                            }
                            val avgSet = BarDataSet(listOf(avg), "한국인 평균").apply {
                                color = android.graphics.Color.LTGRAY
                                setValueTextColor(android.graphics.Color.DKGRAY)
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
                            axisLeft.isEnabled = false
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
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // 4번 차트
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .onGloballyPositioned { coords ->
                    val y = coords.positionInWindow().y
                    val screenHeight = context.resources.displayMetrics.heightPixels
                    if (!chart4Visible && y in 0f..screenHeight.toFloat()) {
                        chart4Visible = true
                    }
                }
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = chart4Visible,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(700))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        com.github.mikephil.charting.charts.BarChart(ctx).apply {
                            val my = BarEntry(0f, todayKcal.toFloat())
                            val male = BarEntry(1f, 2600f)
                            val female = BarEntry(2f, 2100f)
                            val mySet = BarDataSet(listOf(my), "나").apply {
                                color = MainPurple.toArgb()
                                setValueTextColor(android.graphics.Color.DKGRAY)
                                setValueTextSize(10f)
                                setDrawValues(true)
                            }
                            val maleSet = BarDataSet(listOf(male), "남자 평균").apply {
                                color = navyBlue.toArgb()
                                setValueTextColor(android.graphics.Color.DKGRAY)
                                setValueTextSize(10f)
                                setDrawValues(true)
                            }
                            val femaleSet = BarDataSet(listOf(female), "여자 평균").apply {
                                color = wineRed.toArgb()
                                setValueTextColor(android.graphics.Color.DKGRAY)
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
                            axisLeft.isEnabled = false
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
                )
            }
        }
    }
}

@Composable
fun ChartHeader(iconRes: Int, text: androidx.compose.ui.text.AnnotatedString) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // 왼쪽 캐릭터 아이콘
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(6.dp))

        // 말풍선
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, shape = BubbleShape(24f))
                .background(color = Color.White, shape = BubbleShape(24f))
                .padding(12.dp)
        ) {
            Text(
                text = text,
                fontFamily = Pretendard,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color.Black
            )
        }
    }
}
