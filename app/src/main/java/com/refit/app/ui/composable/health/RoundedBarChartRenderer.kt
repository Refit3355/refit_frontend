package com.refit.app.ui.composable.health

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.max
import kotlin.math.min

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val barRect = RectF()
    private val path = Path()
    private val baseRadiusPx = Utils.convertDpToPixel(8f)

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        val drawBorder = dataSet.barBorderWidth > 0f

        val buffer = mBarBuffers[index]
        buffer.setPhases(mAnimator.phaseX, mAnimator.phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)

        var j = 0
        while (j < buffer.size()) {
            val left = buffer.buffer[j]
            val top = buffer.buffer[j + 1]
            val right = buffer.buffer[j + 2]
            val bottom = buffer.buffer[j + 3]

            barRect.set(left, top, right, bottom)
            drawRoundedBar(c, dataSet.getColor(j / 4), barRect, baseRadiusPx, mRenderPaint)

            if (drawBorder) c.drawRect(barRect, mBarBorderPaint)
            j += 4
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<out Highlight>) {
        val barData = mChart.barData

        for (high in indices) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e: BarEntry? = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) continue

            val trans = mChart.getTransformer(set.axisDependency)

            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha

            val y1 = e!!.y
            val y2 = 0f

            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
            val barRect = mBarRect

            drawRoundedBar(c, set.highLightColor, barRect, baseRadiusPx, mHighlightPaint)

            high.setDraw(barRect.centerX(), barRect.top)
        }
    }

    private fun drawRoundedBar(
        c: Canvas,
        color: Int,
        rect: RectF,
        baseRadius: Float,
        paint: android.graphics.Paint
    ) {
        val h = rect.height()
        val r = max(0f, min(baseRadius, h / 2f - 1f))
        paint.color = color

        if (r <= 0f) {
            c.drawRect(rect, paint)
        } else {
            val radii = floatArrayOf(
                r, r,  // 좌상
                r, r,  // 우상
                0f, 0f, // 우하
                0f, 0f  // 좌하
            )
            path.reset()
            path.addRoundRect(rect, radii, Path.Direction.CW)
            c.drawPath(path, paint)
        }
    }
}
