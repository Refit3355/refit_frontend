package com.refit.app.util.health

import android.graphics.Typeface
import com.github.mikephil.charting.components.LimitLine
import androidx.compose.ui.graphics.toArgb
import com.refit.app.ui.theme.MainPurple

object ChartUtils {
    fun createLimitLine(value: Float, label: String, typeface: Typeface?): LimitLine {
        val purple = MainPurple

        return LimitLine(value, label).apply {
            lineColor = purple.copy(alpha = 0.6f).toArgb()
            lineWidth = 1f
            enableDashedLine(0f, 0f, 0f)

            textColor = purple.toArgb()
            textSize = 14f
            this.typeface = typeface
            labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP

            xOffset = 6f
            yOffset = 2f
        }
    }
}