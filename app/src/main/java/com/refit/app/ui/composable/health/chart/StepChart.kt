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
import com.refit.app.ui.theme.MainPurple
import com.refit.app.util.common.RoundCommaValueFormatter

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
        height = 180.dp,
        visible = chartVisible,
        onVisible = onVisible
    ) { ctx ->
        com.github.mikephil.charting.charts.BarChart(ctx).apply {

            val barDataSet = BarDataSet(stepEntries, "").apply {
                setDrawValues(true)
                setValueTextColor(Color.DarkGray.toArgb())
                setValueTextSize(13f)
                valueFormatter = RoundCommaValueFormatter()

                val neutralTop    = Color(0xFFDADADA)
                val neutralBottom = Color(0xFFF0F0F0)
                val accentTop     = MainPurple.copy(0.4f)
                val accentBottom  = MainPurple.copy(0.4f)

                gradientColors = stepEntries.mapIndexed { idx, _ ->
                    if (idx == stepEntries.lastIndex) {
                        GradientColor(accentTop.toArgb(), accentBottom.toArgb())
                    } else {
                        GradientColor(neutralTop.toArgb(), neutralBottom.toArgb())
                    }
                }
            }

            data = BarData(barDataSet).apply { barWidth = 0.82f }
            setMinOffset(0f)
            setViewPortOffsets(0f, 8f, 0f, 18f)
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)

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

            axisLeft.apply {
                isEnabled = true
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
                removeAllLimitLines()
                val maxVal = maxOf(maxSteps, koreanAvgSteps)
                axisMinimum = 0f
                axisMaximum = (maxVal * 1.1f)
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                yOffset = 6f
                setTextColor(Color.DarkGray.toArgb())
                valueFormatter = indexFormatter
                textSize = 13f
                typeface = pretendardBold
            }

            if (chartVisible) {
                animateY(1000, com.github.mikephil.charting.animation.Easing.EaseOutCubic)
            }
        }
    }
}
