package com.refit.app.ui.screen

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.refit.app.R
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.weather.modelAndView.WeatherViewModel
import com.refit.app.ui.composable.weather.WeatherChart

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

        val chartTemps = remember(uiState.maxTemps, uiState.temperature) {
            uiState.maxTemps.toMutableList().apply {
                if (uiState.temperature != null && isNotEmpty()) {
                    this[lastIndex] = uiState.temperature!!
                }
            }
        }

        WeatherChart(title = "최근 기온 기록", values = chartTemps, unit = "℃", iconRes = R.drawable.jellbbo_sunny)
        WeatherChart(title = "최근 습도 기록", values = uiState.humidities, unit = "%", iconRes = R.drawable.jellbbo_humid)
        WeatherChart(title = "최근 강수량 기록", values = uiState.precipitations, unit = "mm", iconRes = R.drawable.jellbbo_rainy)
        WeatherChart(title = "최근 적설량 기록", values = uiState.snowfalls, unit = "cm", iconRes = R.drawable.jellbbo_snow)
    }
}