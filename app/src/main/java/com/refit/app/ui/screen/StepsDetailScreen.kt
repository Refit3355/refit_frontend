package com.refit.app.ui.screen

import android.graphics.Color
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.LineChart as MpLineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.ui.viewmodel.health.HealthViewModel
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.HealthConnectClient
import com.refit.app.data.health.HealthRepo
import com.github.mikephil.charting.components.Legend

@Composable
fun StepsDetailScreen(
    navController: NavHostController,
    vm: HealthViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val rows = uiState.rows
    val context = LocalContext.current

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        val allGranted = HealthRepo.readPerms.all { granted.contains(it) }
        if (allGranted) {
            Log.d("StepsDetailScreen", "권한 허용 완료 → fetch 호출")
            vm.onPermissionGranted(context)
            vm.onDaysChanged(context, 7)
        } else {
            Log.w("StepsDetailScreen", "권한 거부됨")
        }
    }

    // 화면 진입 시 실행
    LaunchedEffect(Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (HealthRepo.readPerms.all { granted.contains(it) }) {
            Log.d("StepsDetailScreen", "이미 권한 있음 → fetch 호출")
            vm.onPermissionGranted(context)
            vm.onDaysChanged(context, 7)
        } else {
            permissionLauncher.launch(HealthRepo.readPerms)
        }
    }

    // ====== 화면 레이아웃 ======
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 제목 + 마스코트 아이콘
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_doctor),
                contentDescription = "타이틀 젤뽀",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("나의 걸음 수 리포트", style = MaterialTheme.typography.titleMedium.copy(fontFamily = Pretendard))
        }

        Spacer(Modifier.height(16.dp))

        if (rows.isEmpty()) {
            Text("데이터가 없습니다.", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard))
            return
        }

        val todaySteps = rows.lastOrNull()?.steps ?: 0L
        val todayKcal = rows.lastOrNull()?.totalKcal ?: 0.0
        val avgSteps = rows.mapNotNull { it.steps }.average().toInt()
        val avgKcal = rows.mapNotNull { it.totalKcal }.average().toInt()

        val xLabels = rows.mapIndexed { idx, _ ->
            if (idx == rows.size - 1) "오늘"
            else "${rows.size - 1 - idx}일전"
        }
        val indexFormatter = IndexAxisValueFormatter(xLabels)

        // ========== 1번째 차트: 최근 걸음수 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_walk),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("7일간의 내 걸음", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            factory = { ctx ->
                MpLineChart(ctx).apply {
                    val entries = rows.mapIndexed { idx, row ->
                        Entry(idx.toFloat(), (row.steps ?: 0L).toFloat())
                    }
                    val dataSet = LineDataSet(entries, "걸음수").apply {
                        color = MainPurple.toArgb()
                        valueTextColor = Color.DKGRAY
                        lineWidth = 2f
                        setDrawCircles(true)
                        setCircleColor(MainPurple.toArgb())
                    }
                    data = LineData(dataSet)
                    description.isEnabled = false
                    axisRight.isEnabled = false
                    axisLeft.setDrawGridLines(false)

                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.valueFormatter = indexFormatter
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 2번째 차트: 최근 소비 칼로리 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_walk),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("7일간의 칼로리 소모", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            factory = { ctx ->
                MpLineChart(ctx).apply {
                    val entries = rows.mapIndexed { idx, row ->
                        Entry(idx.toFloat(), (row.totalKcal ?: 0.0).toFloat())
                    }
                    val dataSet = LineDataSet(entries, "소모 칼로리(kcal)").apply {
                        color = MainPurple.toArgb()
                        valueTextColor = Color.DKGRAY
                        lineWidth = 2f
                        setDrawCircles(true)
                        setCircleColor(MainPurple.toArgb())
                    }
                    data = LineData(dataSet)
                    description.isEnabled = false
                    axisRight.isEnabled = false
                    axisLeft.setDrawGridLines(false)

                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.valueFormatter = indexFormatter
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 3번째 차트: 한국인 평균 걸음 비교 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_walk),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("오늘 걸음 수 vs 한국인 평균", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { ctx ->
                com.github.mikephil.charting.charts.BarChart(ctx).apply {
                    val my = BarEntry(0f, todaySteps.toFloat())
                    val koreanAvg = BarEntry(1f, 9611f)

                    val mySet = BarDataSet(listOf(my), "나의 오늘 걸음수").apply {
                        color = MainPurple.toArgb()
                        valueTextColor = Color.DKGRAY
                    }
                    val avgSet = BarDataSet(listOf(koreanAvg), "한국인 일평균 걸음수").apply {
                        color = Color.LTGRAY
                        valueTextColor = Color.DKGRAY
                    }

                    data = BarData(mySet, avgSet)
                    barData.barWidth = 0.4f
                    groupBars(0f, 0.2f, 0f)

                    description.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.isEnabled = false
                    axisLeft.setDrawGridLines(false)

                    legend.isEnabled = true
                    legend.form = Legend.LegendForm.SQUARE
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    legend.orientation = Legend.LegendOrientation.HORIZONTAL
                    legend.setDrawInside(false)
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 4번째 차트: 한국인 평균 활동 칼로리 비교 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_walk),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("오늘 칼로리 소모량 vs 한국인 평균", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { ctx ->
                com.github.mikephil.charting.charts.BarChart(ctx).apply {
                    val my = BarEntry(0f, todayKcal.toFloat())
                    val koreanAvgKcal = BarEntry(1f, 350f)

                    val mySet = BarDataSet(listOf(my), "오늘 총 소모 칼로리").apply {
                        color = MainPurple.toArgb()
                        valueTextColor = Color.DKGRAY
                    }
                    val avgSet = BarDataSet(listOf(koreanAvgKcal), "한국인 일평균 소모").apply {
                        color = Color.LTGRAY
                        valueTextColor = Color.DKGRAY
                    }

                    data = BarData(mySet, avgSet)
                    barData.barWidth = 0.4f
                    groupBars(0f, 0.2f, 0f)

                    description.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.isEnabled = false
                    axisLeft.setDrawGridLines(false)

                    legend.isEnabled = true
                    legend.form = Legend.LegendForm.SQUARE
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    legend.orientation = Legend.LegendOrientation.HORIZONTAL
                    legend.setDrawInside(false)
                }
            }
        )
    }
}
