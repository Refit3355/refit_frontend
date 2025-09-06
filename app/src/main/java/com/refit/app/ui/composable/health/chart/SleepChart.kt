package com.refit.app.ui.composable.health.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.health.ChartUtils
import com.refit.app.util.health.SleepFormatter

@Composable
fun SleepChart(
    sleepEntries: List<BarEntry>,
    koreanAvgSleep: Int,
    pretendardBold: Typeface?,
    indexFormatter: IndexAxisValueFormatter,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(
        height = androidx.compose.ui.unit.Dp(220f),
        visible = chartVisible,
        onVisible = onVisible
    ) { context ->
        com.github.mikephil.charting.charts.BarChart(context).apply {
            val barDataSet = BarDataSet(sleepEntries, "").apply {
                setDrawValues(true)
                setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                setValueTextSize(12f)
                colors = sleepEntries.mapIndexed { idx, _ ->
                    if (idx == sleepEntries.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                }
                valueFormatter = SleepFormatter()
            }
            data = BarData(barDataSet).apply { barWidth = 0.4f }
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = true
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
                axisMinimum = 0f
                val maxVal = maxOf(
                    sleepEntries.maxOfOrNull { it.y } ?: 0f,
                    koreanAvgSleep.toFloat()
                )
                axisMaximum = maxVal * 1.1f
                removeAllLimitLines()
                addLimitLine(
                    ChartUtils.createLimitLine(
                        koreanAvgSleep.toFloat(),
                        "일반인 평균 ${koreanAvgSleep / 60}h ${koreanAvgSleep % 60}m",
                        pretendardBold
                    )
                )
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                setTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                valueFormatter = indexFormatter
            }
            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
