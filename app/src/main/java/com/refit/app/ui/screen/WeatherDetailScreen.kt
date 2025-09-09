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
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.weather.chart.WeatherHumidChart
import com.refit.app.ui.composable.weather.chart.WeatherPrecipChart
import com.refit.app.ui.composable.weather.chart.WeatherSnowChart
import com.refit.app.ui.composable.weather.chart.WeatherTempChart
import com.refit.app.ui.theme.MainPurple
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import com.refit.app.ui.composable.health.chartHeader.TitleLine
import com.refit.app.ui.composable.health.chartHeader.InfoCallout

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
                append("${nickname}님,")
                append("오늘 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("날씨") }
                append("에 \n 맞춰 루틴을 가볍게 조정해봐요.")
            }
        )

        Spacer(Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // ---------------- 1) 기온 ----------------
            TitleLine(iconRes = R.drawable.jellbbo_sunny, title = "최근 7일 기온")
            Spacer(Modifier.height(10.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("기온 변화는 피부 수분 상태에 영향을 줘요. ")
                    append("온도가 낮아지면 혈관 수축으로 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈류량") }
                    append("이 줄고,\n높아지면 땀·피지로 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("수분 손실") }
                    append("이 커질 수 있어요.")
                }
            )
            Spacer(Modifier.height(16.dp))
            WeatherTempChart(
                temps = temps,
                indexFormatter = indexFormatter,
                pretendardBold = pretendardBold,
                chartVisible = chart1Visible,
                onVisible = { chart1Visible = true }
            )

            Spacer(Modifier.height(80.dp))

            // ---------------- 2) 습도 ----------------
            TitleLine(iconRes = R.drawable.jellbbo_humid, title = "최근 7일 습도")
            Spacer(Modifier.height(10.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("습도가 낮으면 각질층의 NMF 농도가 떨어지고 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 장벽") }
                    append("이 약해질 수 있어요.")
                }
            )
            Spacer(Modifier.height(16.dp))
            WeatherHumidChart(
                humids = humids,
                indexFormatter = indexFormatter,
                pretendardBold = pretendardBold,
                chartVisible = chart2Visible,
                onVisible = { chart2Visible = true }
            )

            Spacer(Modifier.height(80.dp))

            // ---------------- 3) 강수량 ----------------
            TitleLine(iconRes = R.drawable.jellbbo_rainy, title = "최근 7일 강수량")
            Spacer(Modifier.height(10.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("비가 잦은 날은 오염 물질이 피부에 더 쉽게 붙어요. ")
                    append("세안·보습을 챙겨 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("모공 막힘/염증") }
                    append("을 예방해요.")
                }
            )
            Spacer(Modifier.height(16.dp))
            WeatherPrecipChart(
                precs = precs,
                indexFormatter = indexFormatter,
                pretendardBold = pretendardBold,
                chartVisible = chart3Visible,
                onVisible = { chart3Visible = true }
            )

            Spacer(Modifier.height(80.dp))

            // ---------------- 4) 적설량 ----------------
            TitleLine(iconRes = R.drawable.jellbbo_snow, title = "최근 7일 적설량")
            Spacer(Modifier.height(10.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("눈 오는 날은 실내 난방으로 공기가 건조해져요. ")
                    append("저녁엔 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("보습 레이어링") }
                    append("을 권장해요.")
                }
            )
            Spacer(Modifier.height(10.dp))
            WeatherSnowChart(
                snows = snows,
                indexFormatter = indexFormatter,
                pretendardBold = pretendardBold,
                chartVisible = chart4Visible,
                onVisible = { chart4Visible = true }
            )
        }
    }
}
