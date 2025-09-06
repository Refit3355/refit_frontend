package com.refit.app.ui.composable.health.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.health.SleepFormatter

@Composable
fun SleepCompareRecChart(
    yesterdaySleep: Long,
    recommendedSleep: Int,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(
        height = androidx.compose.ui.unit.Dp(200f),
        visible = chartVisible,
        onVisible = onVisible
    ) { context ->
        com.github.mikephil.charting.charts.BarChart(context).apply {
            val my = BarEntry(0f, yesterdaySleep.toFloat())
            val rec = BarEntry(1f, recommendedSleep.toFloat())
            val mySet = BarDataSet(listOf(my), "나").apply {
                color = MainPurple.toArgb()
                setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                setValueTextSize(10f)
                setDrawValues(true)
                valueFormatter = SleepFormatter()
            }
            val recSet = BarDataSet(listOf(rec), "권장 수면").apply {
                color = androidx.compose.ui.graphics.Color.LightGray.toArgb()
                setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                setValueTextSize(10f)
                setDrawValues(true)
                valueFormatter = SleepFormatter()
            }
            val groupSpace = 0.4f
            val barSpace = 0.05f
            val barWidth = 0.2f
            data = BarData(mySet, recSet).apply { this.barWidth = barWidth }
            val groupWidth = data.getGroupWidth(groupSpace, barSpace)
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = groupWidth
            groupBars(0f, groupSpace, barSpace)
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
            description.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                val maxVal = maxOf(yesterdaySleep.toFloat(), recommendedSleep.toFloat())
                axisMaximum = maxVal * 1.1f
            }
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            legend.isEnabled = true
            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
