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
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.R
import com.refit.app.data.product.modelAndView.RecommendationViewModel
import com.refit.app.data.weather.modelAndView.WeatherViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.ChartHeader
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.home.HomeProductRow
import com.refit.app.ui.composable.home.SectionHeader
import com.refit.app.ui.composable.weather.chart.WeatherHumidChart
import com.refit.app.ui.composable.weather.chart.WeatherPrecipChart
import com.refit.app.ui.composable.weather.chart.WeatherSnowChart
import com.refit.app.ui.composable.weather.chart.WeatherTempChart
import com.refit.app.ui.theme.MainPurple
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

    val recommendVm: RecommendationViewModel = viewModel()
    val recommendState by recommendVm.state.collectAsState()

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { vm.loadWeather(it.latitude, it.longitude) }
            }
        } else {
            permissionState.launchPermissionRequest()
        }
        recommendVm.loadRecommendations(type = 0, limit = 100)
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
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("기온 변화(℃)") }
                append("를 확인했어요.\n")
                append("기온이 낮아지면 혈관 수축으로 피부의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈류량") }
                append("이 줄어들고,\n높아지면 땀과 피지 분비가 증가해 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("수분 손실") }
                append("이 커질 수 있어요.")
            }
        )
        WeatherTempChart(
            temps = temps,
            indexFormatter = indexFormatter,
            pretendardBold = pretendardBold,
            chartVisible = chart1Visible,
            onVisible = { chart1Visible = true }
        )

        Spacer(Modifier.height(32.dp))

        // ---------------- 2번 차트: 습도 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_humid,
            text = buildAnnotatedString {
                append("최근 7일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("습도(%)") }
                append(" 기록이에요.\n")
                append("습도가 40% 이하로 떨어지면 각질층의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("NMF(천연보습인자)") }
                append(" 농도가 낮아지고,\n")
                append("수분 증발량이 증가해")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 장벽")}
                append("이 쉽게 손상될 수 있습니다.")
            }
        )
        WeatherHumidChart(
            humids = humids,
            indexFormatter = indexFormatter,
            pretendardBold = pretendardBold,
            chartVisible = chart2Visible,
            onVisible = { chart2Visible = true }
        )

        Spacer(Modifier.height(32.dp))

        // ---------------- 3번 차트: 강수량 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_rainy,
            text = buildAnnotatedString {
                append("최근 일주일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("강수량(mm)") }
                append("을 확인했어요.\n")
                append("비가 잦으면 대기 중 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("오염 물질") }
                append("이 피부에 붙기 쉬워\n")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("모공 막힘") }
                append("과")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("염증 반응") }
                append("을 유발할 수 있으니 세안을 철저히 해야 해요.")
            }
        )
        WeatherPrecipChart(
            precs = precs,
            indexFormatter = indexFormatter,
            pretendardBold = pretendardBold,
            chartVisible = chart3Visible,
            onVisible = { chart3Visible = true }
        )

        Spacer(Modifier.height(32.dp))

        // ---------------- 4번 차트: 적설량 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_snow,
            text = buildAnnotatedString {
                append("최근 7일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("적설량(cm)") }
                append(" 데이터에요.\n")
                append("눈이 많이 오는 날은 대기 습도가 낮고 난방 사용이 늘어나\n")
                append("피부의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("수분 증발") }
                append("이 증가하여 건조·가려움이 심해질 수 있어요.")
            }
        )
        WeatherSnowChart(
            snows = snows,
            indexFormatter = indexFormatter,
            pretendardBold = pretendardBold,
            chartVisible = chart4Visible,
            onVisible = { chart4Visible = true }
        )

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
                navController.navigate("recommendation/2")
            }
        )

        HomeProductRow(
            products = recommendState.items.take(10),
            onClick = { p -> navController.navigate("product/${p.id}") }
        )
    }
}
