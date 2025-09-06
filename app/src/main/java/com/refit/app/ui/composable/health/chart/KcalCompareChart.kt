package com.refit.app.ui.composable.health.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.data.*
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple

@Composable
fun KcalCompareChart(
    todayKcal: Float,
    navyBlue: Color,
    wineRed: Color,
    kcalAvg: Float,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(
        height = 200.dp,
        visible = chartVisible,
        onVisible = onVisible
    ) { ctx ->
        com.github.mikephil.charting.charts.BarChart(ctx).apply {
            val my = BarEntry(0f, todayKcal)
            val male = BarEntry(1f, 2600f)
            val female = BarEntry(2f, 2100f)
            val mySet = BarDataSet(listOf(my), "나").apply {
                color = MainPurple.toArgb()
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(10f)
                setDrawValues(true)
            }
            val maleSet = BarDataSet(listOf(male), "남자 평균").apply {
                color = navyBlue.toArgb()
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(10f)
                setDrawValues(true)
            }
            val femaleSet = BarDataSet(listOf(female), "여자 평균").apply {
                color = wineRed.toArgb()
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(10f)
                setDrawValues(true)
            }
            val groupSpace = 0.3f
            val barSpace = 0.05f
            val barWidth = 0.2f
            data = BarData(mySet, maleSet, femaleSet).apply { this.barWidth = barWidth }
            val groupWidth = data.getGroupWidth(groupSpace, barSpace)
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = groupWidth
            groupBars(0f, groupSpace, barSpace)
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                val maxVal = maxOf(todayKcal, 2600f, 2100f, kcalAvg)
                axisMinimum = 0f
                axisMaximum = (maxVal * 1.1f)
            }
            xAxis.apply {
                isEnabled = false
                setDrawGridLines(false)
            }
            legend.isEnabled = true
            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
