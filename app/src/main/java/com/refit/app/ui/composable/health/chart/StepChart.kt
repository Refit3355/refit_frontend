package com.refit.app.ui.composable.health.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.common.RoundCommaValueFormatter
import com.refit.app.util.health.ChartUtils

@Composable
fun StepChart(
    stepEntries: List<BarEntry>,
    maxSteps: Float,
    koreanAvgSteps: Float,
    pretendardBold: Typeface?,
    indexFormatter: IndexAxisValueFormatter,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(
        height = 220.dp,
        visible = chartVisible,
        onVisible = onVisible
    ) { ctx ->
        com.github.mikephil.charting.charts.BarChart(ctx).apply {

            val barDataSet = BarDataSet(stepEntries, "").apply {
                setDrawValues(true)
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(10f)
                colors = stepEntries.mapIndexed { idx, _ ->
                    if (idx == stepEntries.size - 1) MainPurple.toArgb() else Color.LightGray.toArgb()
                }
                valueFormatter = RoundCommaValueFormatter()
            }
            data = BarData(barDataSet).apply { barWidth = 0.82f }
            setFitBars(true)
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = true
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
                removeAllLimitLines()
                addLimitLine(
                    ChartUtils.createLimitLine(koreanAvgSteps, "일반인 평균 ${koreanAvgSteps.toInt()} 걸음", pretendardBold)
                )
                val maxVal = maxOf(maxSteps, koreanAvgSteps)
                axisMinimum = 0f
                axisMaximum = (maxVal * 1.1f)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                setTextColor(Color.DarkGray.toArgb())
                valueFormatter = indexFormatter
            }
            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
