package com.refit.app.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.R
import com.refit.app.data.weather.modelAndView.WeatherViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.ChartHeader
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.util.health.ChartUtils
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun WeatherDetailScreen(
    navController: NavController,
    vm: WeatherViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val ctx = LocalContext.current

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { vm.loadWeather(it.latitude, it.longitude) }
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    if (uiState.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (uiState.error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("에러: ${uiState.error}", color = MaterialTheme.colorScheme.error)
        }
        return
    }
    if (uiState.temperature == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("날씨 데이터를 가져오는 중...")
        }
        return
    }

    val pretendardBold: Typeface? = ResourcesCompat.getFont(ctx, R.font.pretendard_bold)
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val xLabels = days.map { if (it == today) "오늘" else it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN) }
    val indexFormatter = IndexAxisValueFormatter(xLabels)

    // 데이터
    val temps = uiState.maxTemps.takeLast(7).mapIndexed { idx, v -> BarEntry(idx.toFloat(), v.toFloat()) }
    val humids = uiState.humidities.takeLast(7).mapIndexed { idx, v -> BarEntry(idx.toFloat(), v.toFloat()) }
    val precs = uiState.precipitations.takeLast(7).mapIndexed { idx, v -> BarEntry(idx.toFloat(), v.toFloat()) }
    val snows = uiState.snowfalls.takeLast(7).mapIndexed { idx, v -> BarEntry(idx.toFloat(), v.toFloat()) }

    var chart1Visible by remember { mutableStateOf(false) }
    var chart2Visible by remember { mutableStateOf(false) }
    var chart3Visible by remember { mutableStateOf(false) }
    var chart4Visible by remember { mutableStateOf(false) }

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

        val nickname = UserPrefs.getNickname() ?: "사용자"
        GifCard(
            nickname = nickname,
            imageLoader = imageLoader,
            gifRes = R.raw.weather_jellbbo,
            message = buildAnnotatedString {
                append("${nickname}님, 오늘은\n")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("날씨") }
                append("도 함께 살펴봐요!")
            }
        )

        Spacer(Modifier.height(20.dp))

        // ---------------- 1번 차트: 기온 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_sunny,
            text = buildAnnotatedString {
                append("최근 7일 동안의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("기온 변화") }
                append("를 확인했어요.\n")
                append("기온이 낮아지면 혈관 수축으로 피부의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈류량") }
                append("이 줄어들고,\n높아지면 땀과 피지 분비가 증가해 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("수분 손실") }
                append("이 커질 수 있어요.")
            }
        )
        ChartBox(
            height = 220.dp,
            visible = chart1Visible,
            onVisible = { chart1Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val dataSet = BarDataSet(temps, "").apply {
                    setDrawValues(true)
                    setValueTextSize(12f)
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    colors = temps.mapIndexed { idx, _ ->
                        if (idx == temps.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    }
                }
                data = BarData(dataSet).apply { barWidth = 0.4f }
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    axisMinimum = 0f
                    axisMaximum = (temps.maxOfOrNull { it.y } ?: 0f) * 1.1f
                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(25f, "쾌적 기준 25℃", pretendardBold)
                    )
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = indexFormatter
                }
                if (chart1Visible) animateY(1000)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 2번 차트: 습도 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_humid,
            text = buildAnnotatedString {
                append("최근 7일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("습도") }
                append(" 기록이에요.\n")
                append("습도가 40% 이하로 떨어지면 각질층의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("NMF(천연보습인자)") }
                append(" 농도가 낮아지고,\n")
                append("수분 증발량이 증가해 피부 장벽이 쉽게 손상될 수 있습니다.")
            }
        )
        ChartBox(
            height = 220.dp,
            visible = chart2Visible,
            onVisible = { chart2Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val dataSet = BarDataSet(humids, "").apply {
                    setDrawValues(true)
                    setValueTextSize(12f)
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    colors = humids.mapIndexed { idx, _ ->
                        if (idx == humids.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    }
                }
                data = BarData(dataSet).apply { barWidth = 0.4f }
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    axisMinimum = 0f
                    axisMaximum = 100f
                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(50f, "적정습도 50%", pretendardBold)
                    )
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = indexFormatter
                }
                if (chart2Visible) animateY(1000)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 3번 차트: 강수량 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_rainy,
            text = buildAnnotatedString {
                append("최근 일주일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("강수량") }
                append("을 확인했어요.\n")
                append("비가 잦으면 대기 중 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("오염 물질") }
                append("이 피부에 붙기 쉬워 모공 막힘과\n")
                append("염증 반응을 유발할 수 있으니 세안을 철저히 해야 해요.")
            }
        )
        ChartBox(
            height = 200.dp,
            visible = chart3Visible,
            onVisible = { chart3Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val dataSet = BarDataSet(precs, "").apply {
                    setDrawValues(true)
                    setValueTextSize(12f)
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    colors = precs.mapIndexed { idx, _ ->
                        if (idx == precs.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    }
                }
                data = BarData(dataSet).apply { barWidth = 0.4f }
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    axisMinimum = 0f
                    axisMaximum = (precs.maxOfOrNull { it.y } ?: 0f) * 1.1f  // 강수량 최대치
                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(10f, "기준 10mm", pretendardBold)
                    )
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = indexFormatter
                }
                if (chart3Visible) animateY(1000)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 4번 차트: 적설량 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_snow,
            text = buildAnnotatedString {
                append("최근 7일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("적설량") }
                append(" 데이터입니다.\n")
                append("눈이 많이 오는 날은 대기 습도가 낮고 난방 사용이 늘어나\n")
                append("피부의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("수분 증발") }
                append("이 증가하여 건조·가려움이 심해질 수 있어요.")
            }
        )
        ChartBox(
            height = 200.dp,
            visible = chart4Visible,
            onVisible = { chart4Visible = true }
        ) { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                val dataSet = BarDataSet(snows, "").apply {
                    setDrawValues(true)
                    setValueTextSize(12f)
                    setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                    colors = snows.mapIndexed { idx, _ ->
                        if (idx == snows.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                    }
                }
                data = BarData(dataSet).apply { barWidth = 0.4f }
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.apply {
                    isEnabled = false
                    axisMinimum = 0f
                    axisMaximum = (snows.maxOfOrNull { it.y } ?: 0f) * 1.1f  // 적설량 최대치
                    removeAllLimitLines()
                    addLimitLine(
                        ChartUtils.createLimitLine(5f, "주의 기준 5cm", pretendardBold)
                    )
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = indexFormatter
                }
                if (chart4Visible) animateY(1000)
            }
        }
    }
}
