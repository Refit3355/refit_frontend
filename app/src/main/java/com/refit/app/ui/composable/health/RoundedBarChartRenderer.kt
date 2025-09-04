package com.refit.app.ui.composable.health

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val mBarShadowRectBuffer = RectF()

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor

            val barData = mChart.barData

            val barWidthHalf = barData.barWidth / 2.0f
            var x: Int = 0
            while (x < Math.min(
                    Math.ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt(),
                    dataSet.entryCount
                )
            ) {
                val e = dataSet.getEntryForIndex(x) as BarEntry

                val xPos = e.x

                mBarShadowRectBuffer.left = xPos - barWidthHalf
                mBarShadowRectBuffer.right = xPos + barWidthHalf

                trans.rectValueToPixel(mBarShadowRectBuffer)

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    x++
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                c.drawRoundRect(
                    mBarShadowRectBuffer, 20f, 20f, mShadowPaint
                )
                x++
            }
        }

        mBarBuffers[index].apply {
            setPhases(phaseX, phaseY)
            setDataSet(index)
            setInverted(mChart.isInverted(dataSet.axisDependency))
            setBarWidth(mChart.barData.barWidth)

            feed(dataSet)

            trans.pointValuesToPixel(buffer)

            for (j in 0 until buffer.size step 4) {
                if (!mViewPortHandler.isInBoundsLeft(buffer[j + 2])) continue
                if (!mViewPortHandler.isInBoundsRight(buffer[j])) break

                val left = buffer[j]
                val top = buffer[j + 1]
                val right = buffer[j + 2]
                val bottom = buffer[j + 3]

                val paint = mRenderPaint
                paint.color = dataSet.getColor(j / 4)

                // 둥근 모서리 적용
                c.drawRoundRect(RectF(left, top, right, bottom), 30f, 30f, paint)

                if (drawBorder) {
                    c.drawRoundRect(RectF(left, top, right, bottom), 30f, 30f, mBarBorderPaint)
                }
            }
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<com.github.mikephil.charting.highlight.Highlight>) {
        val barData = mChart.barData

        for (high in indices) {
            val set = barData.getDataSetByIndex(high.dataSetIndex) ?: continue
            if (!set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y) ?: continue
            if (!isInBoundsX(e, set)) continue

            val trans = mChart.getTransformer(set.axisDependency)
            mBarRect.set(
                e.x - barData.barWidth / 2f,
                0f,
                e.x + barData.barWidth / 2f,
                e.y
            )
            trans.rectToPixelPhase(mBarRect, mAnimator.phaseY)

            mHighlightPaint.color = android.graphics.Color.argb(70, 0, 0, 0)

            c.drawRoundRect(mBarRect, 30f, 30f, mHighlightPaint)
        }
    }

}
