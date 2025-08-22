package com.refit.app.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart as MpLineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.refit.app.R
import com.refit.app.ui.theme.Pretendard
import com.refit.app.ui.theme.MainPurple
import com.refit.app.data.weather.modelAndView.WeatherViewModel

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun WeatherDetailScreen(
    navController: NavController,
    vm: WeatherViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val ctx = LocalContext.current

    // 위치 권한 상태
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    vm.loadWeather(it.latitude, it.longitude)
                }
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_doctor),
                contentDescription = "타이틀 젤뽀",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("날씨 리포트", style = MaterialTheme.typography.titleMedium.copy(fontFamily = Pretendard))
        }

        Spacer(Modifier.height(16.dp))
        when {
            uiState.loading -> {
                CircularProgressIndicator()
                return
            }
            uiState.error != null -> {
                Text(
                    "에러: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard)
                )
                return
            }
            uiState.temperature == null -> {
                Text(
                    "날씨 데이터를 가져오는 중...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard)
                )
                return
            }
        }

        // 현재 날씨 정보
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = androidx.compose.ui.graphics.Color(0xFFE9E4F2), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    "현재 기온: ${uiState.temperature} ℃",
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard)
                )
                Text(
                    "풍속: ${uiState.windspeed} m/s",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        WeatherChart(title = "최근 기온 기록", values = uiState.maxTemps, unit = "℃", iconRes = R.drawable.jellbbo_sunny)
        WeatherChart(title = "최근 습도 기록", values = uiState.humidities, unit = "%", iconRes = R.drawable.jellbbo_humid)
        WeatherChart(title = "최근 강수량 기록", values = uiState.precipitations, unit = "mm", iconRes = R.drawable.jellbbo_rainy)
        WeatherChart(title = "최근 적설량 기록", values = uiState.snowfalls, unit = "cm", iconRes = R.drawable.jellbbo_snow)
    }
}

@Composable
fun WeatherChart(title: String, values: List<Double>, unit: String, iconRes: Int) {
    Spacer(Modifier.height(24.dp))
    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontFamily = Pretendard))
    }
    Spacer(Modifier.height(8.dp))

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { ctx ->
            MpLineChart(ctx).apply {
                val entries = values.mapIndexed { idx, value ->
                    Entry(idx.toFloat(), value.toFloat())
                }
                val dataSet = LineDataSet(entries, "$title ($unit)").apply {
                    color = MainPurple.hashCode()
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                    setDrawCircles(true)
                    setCircleColor(MainPurple.hashCode())
                    circleRadius = 4f
                }
                data = LineData(dataSet)

                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                legend.isEnabled = false

                val totalDays = values.size
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val idx = value.toInt()
                        val daysAgo = (totalDays - 1) - idx
                        return if (daysAgo == 0) "오늘(예보)" else "${daysAgo}일 전"
                    }
                }

                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}$unit"
                    }
                }
            }
        }
    )
}
