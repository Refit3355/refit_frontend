package com.refit.app.ui.composable.health.chart

import android.graphics.Typeface
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
    ChartBox(height = 220.dp, visible = chartVisible, onVisible = onVisible) { context ->
        com.github.mikephil.charting.charts.BarChart(context).apply {
            val barDataSet = BarDataSet(sleepEntries, "").apply {
                setDrawValues(true)
                setValueTextColor(Color(0xFF424242).toArgb())
                setValueTextSize(13f)
                valueFormatter = SleepFormatter()

                // 그라데이션: 마지막 막대만 보라, 나머지 뉴트럴 그레이
                val neutralTop = Color(0xFFDADADA)
                val neutralBot = Color(0xFFF0F0F0)
                val accentTop  = MainPurple.copy(alpha = 0.4f)
                val accentBot  = MainPurple.copy(alpha = 0.4f)

                gradientColors = sleepEntries.mapIndexed { idx, _ ->
                    if (idx == sleepEntries.lastIndex)
                        GradientColor(accentTop.toArgb(), accentBot.toArgb())
                    else
                        GradientColor(neutralTop.toArgb(), neutralBot.toArgb())
                }
            }

            data = BarData(barDataSet).apply { barWidth = 0.82f }
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)

            // 터치/줌 비활성화
            setTouchEnabled(false)
            setHighlightPerTapEnabled(false)
            setHighlightPerDragEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setOnChartValueSelectedListener(null)

            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false

            // 좌우/하단 오프셋 최소화
            setMinOffset(0f)
            setViewPortOffsets(6f, 8f, 6f, 18f)

            axisLeft.apply {
                isEnabled = true
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
                axisMinimum = 0f
                val maxVal = maxOf(sleepEntries.maxOfOrNull { it.y } ?: 0f, koreanAvgSleep.toFloat())
                axisMaximum = maxVal * 1.1f

                removeAllLimitLines()
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                yOffset = 6f
                setTextColor(Color(0xFF424242).toArgb())
                valueFormatter = indexFormatter
                textSize = 13f
                typeface = pretendardBold

                // 좌우 꽉 차게
                val firstX = sleepEntries.firstOrNull()?.x ?: 0f
                val lastX  = sleepEntries.lastOrNull()?.x ?: 0f
                axisMinimum = firstX - 0.5f
                axisMaximum = lastX + 0.5f
                setAvoidFirstLastClipping(true)
            }

            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
