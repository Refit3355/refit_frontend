package com.refit.app.ui.screen

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.github.mikephil.charting.charts.BarChart as MpBarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.health.modelAndView.HealthViewModel
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle

@Composable
fun SleepDetailScreen(
    navController: NavController,
    vm: HealthViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val rows = uiState.rows
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        if (uiState.permissionGranted) {
            vm.fetch(ctx)
        } else {
            vm.onPermissionGranted(ctx)
        }
    }

    // 데이터
    val allRows = rows
    val yesterdaySleep = allRows.lastOrNull()?.sleepMinutes ?: 0
    val avgSleep = allRows.mapNotNull { it.sleepMinutes }.average().toInt()
    val minSleep = allRows.mapNotNull { it.sleepMinutes }.minOrNull() ?: 0
    val maxSleep = allRows.mapNotNull { it.sleepMinutes }.maxOrNull() ?: 0

    // 한국 평균 & 권장 수면시간
    val koreanAvgSleep = 387    // 6시간 27분
    val recommendedSleep = 480  // 8시간

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ===== 제목 =====
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_doctor),
                contentDescription = "타이틀 젤뽀",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("나의 수면 시간 리포트", style = MaterialTheme.typography.titleMedium.copy(fontFamily = Pretendard))
        }

        Spacer(Modifier.height(16.dp))

        if (allRows.isEmpty()) {
            Text("수면 데이터가 아직 없어요.", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard))
            return
        }

        // ========== 최근 수면 패턴 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_sleep),
                contentDescription = "최근 수면 기록 아이콘",
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("최근 수면 기록", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            factory = { ctx ->
                MpLineChart(ctx).apply {
                    val entries = allRows.mapIndexed { idx, row ->
                        Entry(idx.toFloat(), (row.sleepMinutes ?: 0).toFloat())
                    }
                    data = LineData(LineDataSet(entries, "수면시간").apply {
                        color = MainPurple.hashCode()
                        valueTextColor = Color.BLACK
                        lineWidth = 2f
                        setDrawCircles(true)
                        setCircleColor(MainPurple.hashCode())
                        circleRadius = 4f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val h = value.toInt() / 60
                                val m = value.toInt() % 60
                                return "${h}시간 ${m}분"
                            }
                        }
                    })

                    description.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(false)
                    legend.isEnabled = true

                    val totalSize = allRows.size
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return when (index) {
                                totalSize - 1 -> "오늘"
                                else -> "${totalSize - 1 - index}일 전"
                            }
                        }
                    }
                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val h = value.toInt() / 60
                            val m = value.toInt() % 60
                            return "${h}시간 ${m}분"
                        }
                    }
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 어제 수면량, 최소, 최대 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_sleep),
                contentDescription = "어제 수면 아이콘",
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("어제 내가 잔 시간", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        Text(buildAnnotatedString {
            append("어제는 약 ")
            withStyle(SpanStyle(color = MainPurple)) {
                append("${yesterdaySleep / 60}시간 ${yesterdaySleep % 60}분")
            }
            append("을 잤어요.")
        }, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Pretendard))

        Spacer(Modifier.height(8.dp))
        Column {
            Text(buildAnnotatedString {
                append("가장 적게 잔 날은 ")
                withStyle(SpanStyle(color = ComposeColor.Red)) {
                    append("${minSleep / 60}시간 ${minSleep % 60}분")
                }
            }, style = MaterialTheme.typography.bodySmall.copy(fontFamily = Pretendard))

            Spacer(Modifier.height(4.dp))

            Text(buildAnnotatedString {
                append("가장 많이 잔 날은 ")
                withStyle(SpanStyle(color = ComposeColor.Blue)) {
                    append("${maxSleep / 60}시간 ${maxSleep % 60}분")
                }
                append(" 이에요.")
            }, style = MaterialTheme.typography.bodySmall.copy(fontFamily = Pretendard))
        }

        Spacer(Modifier.height(32.dp))

        // ========== 한국 평균 수면시간과 비교 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_sleep),
                contentDescription = "평균 비교 아이콘",
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("한국인 평균 수면 시간과 비교", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            factory = { ctx ->
                MpBarChart(ctx).apply {
                    val entries = listOf(
                        BarEntry(0f, yesterdaySleep.toFloat()),
                        BarEntry(1f, koreanAvgSleep.toFloat())
                    )
                    data = BarData(
                        BarDataSet(listOf(entries[0]), "어제 수면").apply {
                            color = MainPurple.hashCode(); valueTextColor = Color.BLACK
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    val h = value.toInt() / 60
                                    val m = value.toInt() % 60
                                    return "${h}시간 ${m}분"
                                }
                            }
                        },
                        BarDataSet(listOf(entries[1]), "한국인 평균").apply {
                            color = Color.LTGRAY; valueTextColor = Color.BLACK
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    val h = value.toInt() / 60
                                    val m = value.toInt() % 60
                                    return "${h}시간 ${m}분"
                                }
                            }
                        }
                    ).apply { barWidth = 0.4f; groupBars(0f, 0.3f, 0f) }

                    description.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.isEnabled = false
                    axisLeft.setDrawGridLines(false)
                    legend.isEnabled = true
                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val h = value.toInt() / 60
                            val m = value.toInt() % 60
                            return "${h}시간 ${m}분"
                        }
                    }
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // ========== 권장 수면시간과 비교 ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.jellbbo_sleep),
                contentDescription = "권장 수면 아이콘",
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("권장 수면 시간과 비교", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Pretendard))
        }
        Spacer(Modifier.height(8.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            factory = { ctx ->
                MpBarChart(ctx).apply {
                    val entries = listOf(
                        BarEntry(0f, yesterdaySleep.toFloat()),
                        BarEntry(1f, recommendedSleep.toFloat())
                    )
                    data = BarData(
                        BarDataSet(listOf(entries[0]), "어제 수면").apply {
                            color = MainPurple.hashCode(); valueTextColor = Color.BLACK
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    val h = value.toInt() / 60
                                    val m = value.toInt() % 60
                                    return "${h}시간 ${m}분"
                                }
                            }
                        },
                        BarDataSet(listOf(entries[1]), "권장 수면").apply {
                            color = Color.LTGRAY; valueTextColor = Color.BLACK
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    val h = value.toInt() / 60
                                    val m = value.toInt() % 60
                                    return "${h}시간 ${m}분"
                                }
                            }
                        }
                    ).apply { barWidth = 0.4f; groupBars(0f, 0.3f, 0f) }

                    description.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.isEnabled = false
                    axisLeft.setDrawGridLines(false)
                    legend.isEnabled = true
                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val h = value.toInt() / 60
                            val m = value.toInt() % 60
                            return "${h}시간 ${m}분"
                        }
                    }
                }
            }
        )
    }
}
