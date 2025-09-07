package com.refit.app.ui.composable.weather.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.ui.composable.health.ChartBox
import com.refit.app.ui.composable.health.RoundedBarChartRenderer
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.health.ChartUtils

@Composable
fun WeatherSnowChart(
    snows: List<BarEntry>,
    indexFormatter: IndexAxisValueFormatter,
    pretendardBold: Typeface?,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(
        height = androidx.compose.ui.unit.Dp(200f),
        visible = chartVisible,
        onVisible = onVisible
    ) { ctx ->
        com.github.mikephil.charting.charts.BarChart(ctx).apply {
            val dataSet = BarDataSet(snows, "").apply {
                setDrawValues(true)
                setValueTextSize(12f)
                setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                colors = snows.mapIndexed { idx, _ ->
                    if (idx == snows.size - 1) MainPurple.toArgb() else androidx.compose.ui.graphics.Color.LightGray.toArgb()
                }
            }
            data = BarData(dataSet).apply { barWidth = 0.4f }
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = true
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
                val maxVal = (snows.maxOfOrNull { it.y } ?: 0f)
                axisMinimum = 0f
                axisMaximum = (maxVal.takeIf { it > 0f } ?: 5f) * 1.2f
                removeAllLimitLines()
                addLimitLine(
                    ChartUtils.createLimitLine(5f, "주의 기준 5cm", pretendardBold)
                )
                setDrawLimitLinesBehindData(true)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = indexFormatter
            }
            if (chartVisible) animateY(1000)
        }
    }
}
