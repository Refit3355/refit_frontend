package com.refit.app.ui.screen

import android.graphics.Color
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
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.health.modelAndView.HealthViewModel
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.HealthConnectClient
import com.refit.app.data.health.HealthRepo
import com.github.mikephil.charting.components.Legend
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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

        val today = LocalDate.now()
        val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
        val xLabels = days.map {
            if (it == today) "오늘"
            else it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        }

        // rows와 days를 맞춰 정렬
        val sortedData = days.zip(rows.takeLast(7)).reversed()
        val stepEntries = sortedData.mapIndexed { idx, pair -> BarEntry(idx.toFloat(), (pair.second.steps ?: 0L).toFloat()) }
        val kcalEntries = sortedData.mapIndexed { idx, pair -> BarEntry(idx.toFloat(), (pair.second.totalKcal ?: 0.0).toFloat()) }

        val indexFormatter = IndexAxisValueFormatter(xLabels)

        // ========== 1번째 차트 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.jellbbo_walk), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("7일간의 내 걸음 (단위: 걸음)", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            factory = { ctx ->
                com.github.mikephil.charting.charts.BarChart(ctx).apply {
                    val barDataSet = BarDataSet(stepEntries, "").apply {
                        setDrawValues(true)
                        valueTextColor = Color.DKGRAY
                        valueTextSize = 12f
                        colors = stepEntries.mapIndexed { idx, _ -> if (idx == stepEntries.size - 1) MainPurple.toArgb() else Color.LTGRAY }
                        valueFormatter = object : ValueFormatter() {
                            override fun getBarLabel(barEntry: BarEntry?): String = "${barEntry?.y?.toInt()}"
                        }
                    }
                    data = BarData(barDataSet).apply { barWidth = 0.4f }
                    renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                    description.isEnabled = false
                    legend.isEnabled = false
                    axisLeft.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.apply {
                        position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f
                        valueFormatter = indexFormatter
                        textColor = Color.DKGRAY
                    }
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 2번째 차트 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.jellbbo_walk), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("7일간의 칼로리 소모 (단위: kcal)", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            factory = { ctx ->
                com.github.mikephil.charting.charts.BarChart(ctx).apply {
                    val barDataSet = BarDataSet(kcalEntries, "").apply {
                        setDrawValues(true)
                        valueTextColor = Color.DKGRAY
                        valueTextSize = 12f
                        colors = kcalEntries.mapIndexed { idx, _ -> if (idx == kcalEntries.size - 1) MainPurple.toArgb() else Color.LTGRAY }
                        valueFormatter = object : ValueFormatter() {
                            override fun getBarLabel(barEntry: BarEntry?): String = "${barEntry?.y?.toInt()}"
                        }
                    }
                    data = BarData(barDataSet).apply { barWidth = 0.4f }
                    renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
                    description.isEnabled = false
                    legend.isEnabled = false
                    axisLeft.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.apply {
                        position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f
                        valueFormatter = indexFormatter
                        textColor = Color.DKGRAY
                    }
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 3번째 차트 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.jellbbo_walk), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("오늘 걸음 수 vs 한국인 평균 (단위: 걸음)", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            factory = { ctx ->
                com.github.mikephil.charting.charts.BarChart(ctx).apply {
                    val my = BarEntry(0f, todaySteps.toFloat())
                    val koreanAvg = BarEntry(1f, 9611f)

                    val mySet = BarDataSet(listOf(my), "나").apply {
                        color = MainPurple.toArgb()
                        valueTextColor = Color.DKGRAY
                        valueTextSize = 10f
                        setDrawValues(true)
                    }
                    val avgSet = BarDataSet(listOf(koreanAvg), "한국인 평균").apply {
                        color = Color.LTGRAY
                        valueTextColor = Color.DKGRAY
                        valueTextSize = 10f
                        setDrawValues(true)
                    }

                    val groupSpace = 0.4f
                    val barSpace = 0.05f
                    val barWidth = 0.2f

                    data = BarData(mySet, avgSet).apply {
                        this.barWidth = barWidth
                    }

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
                    legend.form = Legend.LegendForm.SQUARE
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    legend.orientation = Legend.LegendOrientation.HORIZONTAL
                    legend.setDrawInside(false)
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 4번째 차트 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.jellbbo_walk), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("오늘 칼로리 소모량 vs 한국인 평균 (단위: kcal)", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            factory = { ctx ->
                com.github.mikephil.charting.charts.BarChart(ctx).apply {
                    val my = BarEntry(0f, todayKcal.toFloat())
                    val koreanAvgKcal = BarEntry(1f, 350f)

                    val mySet = BarDataSet(listOf(my), "나").apply {
                        color = MainPurple.toArgb()
                        valueTextColor = Color.DKGRAY
                        valueTextSize = 10f
                        setDrawValues(true)
                    }
                    val avgSet = BarDataSet(listOf(koreanAvgKcal), "한국인 평균").apply {
                        color = Color.LTGRAY
                        valueTextColor = Color.DKGRAY
                        valueTextSize = 10f
                        setDrawValues(true)
                    }

                    val groupSpace = 0.4f
                    val barSpace = 0.05f
                    val barWidth = 0.2f

                    data = BarData(mySet, avgSet).apply {
                        this.barWidth = barWidth
                    }

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
                    legend.form = Legend.LegendForm.SQUARE
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    legend.orientation = Legend.LegendOrientation.HORIZONTAL
                    legend.setDrawInside(false)
                }
            }
        )
    }
}
