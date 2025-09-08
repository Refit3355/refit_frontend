package com.refit.app.ui.composable.health.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.model.GradientColor
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.health.SleepFormatter

@Composable
fun SleepCompareAvgChart(
    yesterdaySleep: Long,
    koreanAvgSleep: Int,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(height = 200.dp, visible = chartVisible, onVisible = onVisible) { context ->
        com.github.mikephil.charting.charts.BarChart(context).apply {
            // 1) 단일 DataSet: x=0(나/어제), x=1(한국 평균)
            val entries = listOf(
                BarEntry(0f, yesterdaySleep.toFloat()),
                BarEntry(1f, koreanAvgSleep.toFloat())
            )
            val set = BarDataSet(entries, "").apply {
                setDrawValues(true)
                setValueTextColor(Color(0xFF424242).toArgb())
                setValueTextSize(12f)
                valueFormatter = SleepFormatter()
                gradientColors = listOf(
                    GradientColor(MainPurple.copy(alpha = 0.4f).toArgb(), MainPurple.copy(alpha = 0.4f).toArgb()),
                    GradientColor(Color(0xFFDADADA).toArgb(), Color(0xFFF0F0F0).toArgb())
                )
                highLightAlpha = 0
            }

            data = BarData(set).apply { barWidth = 0.5f }
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)

            // 2) 인터랙션 Off
            setTouchEnabled(false)
            setHighlightPerTapEnabled(false)
            setHighlightPerDragEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setOnChartValueSelectedListener(null)

            // 3) 데코
            description.isEnabled = false
            legend.isEnabled = false

            // 좌우·하단 여백(라벨 1~2줄도 안전)
            setMinOffset(0f)
            setViewPortOffsets(6f, 8f, 6f, 32f)

            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                val maxVal = maxOf(yesterdaySleep.toFloat(), koreanAvgSleep.toFloat())
                axisMaximum = maxVal * 1.1f
            }

            // 4) X축 라벨(막대 밑 이름)
            xAxis.apply {
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(listOf("나", "한국 평균")) // ← 여기서 라벨
                textSize = 13f
                yOffset = 8f
                axisMinimum = -0.5f
                axisMaximum =  1.5f
                setAvoidFirstLastClipping(true)
            }

            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
