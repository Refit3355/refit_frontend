package com.refit.app.ui.composable.health.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.model.GradientColor
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.common.RoundCommaValueFormatter
import com.refit.app.util.health.ChartUtils

@Composable
fun KcalChart(
    kcalEntries: List<BarEntry>,
    maxKcal: Float,
    kcalAvg: Float,
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
            val barDataSet = BarDataSet(kcalEntries, "").apply {
                // 값 라벨 필요하면 유지/조정
                setDrawValues(true)
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(10f)
                valueFormatter = RoundCommaValueFormatter()

                val neutralTop    = Color(0xFFDADADA)
                val neutralBottom = Color(0xFFF0F0F0)

                val accentTop     = MainPurple.copy(0.4f)
                val accentBottom  = MainPurple.copy(0.4f)

                gradientColors = kcalEntries.mapIndexed { idx, _ ->
                    if (idx == kcalEntries.lastIndex) {
                        GradientColor(accentTop.toArgb(), accentBottom.toArgb())
                    } else {
                        GradientColor(neutralTop.toArgb(), neutralBottom.toArgb())
                    }
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
                val maxVal = maxOf(maxKcal, kcalAvg)
                axisMinimum = 0f
                axisMaximum = (maxVal * 1.1f)
                removeAllLimitLines()
                addLimitLine(
                    ChartUtils.createLimitLine(kcalAvg, "일반인 평균 ${kcalAvg.toInt()} kcal", pretendardBold)
                )
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                yOffset = 6f
                setTextColor(Color.DarkGray.toArgb())
                valueFormatter = indexFormatter
                textSize = 12f
                typeface = pretendardBold
            }
            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
