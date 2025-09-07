package com.refit.app.ui.composable.weather.chart

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

@Composable
fun WeatherTempChart(
    temps: List<BarEntry>,
    indexFormatter: IndexAxisValueFormatter,
    pretendardBold: Typeface?,
    chartVisible: Boolean,
    onVisible: () -> Unit
) {
    ChartBox(height = androidx.compose.ui.unit.Dp(220f), visible = chartVisible, onVisible = onVisible) { ctx ->
        com.github.mikephil.charting.charts.BarChart(ctx).apply {
            val neutralTop = androidx.compose.ui.graphics.Color(0xFFDADADA)
            val neutralBot = androidx.compose.ui.graphics.Color(0xFFF0F0F0)
            val accentTop  = MainPurple.copy(alpha = 0.4f)
            val accentBot  = MainPurple.copy(alpha = 0.4f)

            val dataSet = BarDataSet(temps, "").apply {
                setDrawValues(true)
                setValueTextSize(13f)
                setValueTextColor(androidx.compose.ui.graphics.Color.DarkGray.toArgb())
                // 마지막 막대만 보라, 나머지는 뉴트럴 그라데이션
                gradientColors = temps.mapIndexed { idx, _ ->
                    if (idx == temps.lastIndex)
                        com.github.mikephil.charting.model.GradientColor(accentTop.toArgb(), accentBot.toArgb())
                    else
                        com.github.mikephil.charting.model.GradientColor(neutralTop.toArgb(), neutralBot.toArgb())
                }
                highLightAlpha = 0
                valueFormatter = com.refit.app.util.common.RoundCommaValueFormatter()
            }

            data = BarData(dataSet).apply { barWidth = 0.82f }
            renderer = com.refit.app.ui.composable.health.RoundedBarChartRenderer(this, animator, viewPortHandler)

            // 인터랙션 Off
            setTouchEnabled(false); setHighlightPerTapEnabled(false); setHighlightPerDragEnabled(false)
            setScaleEnabled(false); setPinchZoom(false); isDoubleTapToZoomEnabled = false
            setOnChartValueSelectedListener(null)

            description.isEnabled = false; legend.isEnabled = false; axisRight.isEnabled = false

            // 좌우/하단 여백 최소화 + 꽉 차게
            setMinOffset(0f); setViewPortOffsets(6f, 8f, 6f, 18f)

            axisLeft.apply {
                isEnabled = true
                setDrawAxisLine(false); setDrawGridLines(false); setDrawLabels(false)
                axisMinimum = 0f
                axisMaximum = (temps.maxOfOrNull { it.y } ?: 0f) * 1.1f
                removeAllLimitLines()
                // 차트 안 라벨은 비움 → 바깥 ChipRow로 안내
                setDrawLimitLinesBehindData(false)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false); setDrawAxisLine(false)
                granularity = 1f; yOffset = 6f
                valueFormatter = indexFormatter
                textSize = 13f
                // 좌우 꽉 차도록 범위 보정
                val firstX = temps.firstOrNull()?.x ?: 0f
                val lastX  = temps.lastOrNull()?.x ?: 0f
                axisMinimum = firstX - 0.5f
                axisMaximum = lastX + 0.5f
                setAvoidFirstLastClipping(true)
            }

            if (chartVisible) animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
        }
    }
}
