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
import com.refit.app.util.common.RoundCommaValueFormatter

@Composable
fun StepCompareChart(
    todaySteps: Float,
    koreanAvgSteps: Float,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(
        height = 200.dp,
        visible = chartVisible,
        onVisible = onVisible
    ) { ctx ->
        com.github.mikephil.charting.charts.BarChart(ctx).apply {
            // 1) 단일 DataSet에 2개 막대: "나", "평균"
            val entries = listOf(
                BarEntry(0f, todaySteps),
                BarEntry(1f, koreanAvgSteps)
            )
            val set = BarDataSet(entries, "").apply {
                setDrawValues(true)
                valueFormatter = RoundCommaValueFormatter()
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(11f)
                gradientColors = listOf(
                    // "나" (보라 40% 투명)
                    GradientColor(
                        MainPurple.copy(alpha = 0.4f).toArgb(),
                        MainPurple.copy(alpha = 0.4f).toArgb()
                    ),
                    // "평균" (연한 그레이 그라데이션)
                    GradientColor(
                        Color(0xFFDADADA).toArgb(),
                        Color(0xFFF0F0F0).toArgb()
                    )
                )
                highLightAlpha = 0
            }

            data = BarData(set).apply { barWidth = 0.5f }
            setFitBars(true)
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)

            // 2) 터치/줌/하이라이트 비활성화
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

            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                axisMaximum = (maxOf(todaySteps, koreanAvgSteps) * 1.1f)
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            xAxis.apply {
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(listOf("나", "평균"))
                textSize = 13f
            }

            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
