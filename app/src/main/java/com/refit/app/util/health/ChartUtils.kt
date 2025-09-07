package com.refit.app.util.health

import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import com.github.mikephil.charting.components.LimitLine
import androidx.compose.ui.graphics.toArgb

object ChartUtils {
    fun createLimitLine(value: Float, label: String, typeface: Typeface?): LimitLine {
        val purple = Color(0xFF5F0080)

        return LimitLine(value, label).apply {
            lineColor = purple.toArgb()
            lineWidth = 1.5f
            enableDashedLine(10f, 10f, 0f)
            textColor = purple.toArgb()
            textSize = 11f
            this.typeface = typeface
            labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        }
    }
}
