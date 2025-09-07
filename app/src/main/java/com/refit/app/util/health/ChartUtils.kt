package com.refit.app.util.health

import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import com.github.mikephil.charting.components.LimitLine
import androidx.compose.ui.graphics.toArgb

object ChartUtils {
    fun createLimitLine(value: Float, label: String, typeface: Typeface?): LimitLine {
        return LimitLine(value, label).apply {
            lineColor = Color(0, 128, 128).toArgb()
            lineWidth = 1.5f
            enableDashedLine(10f, 10f, 0f)
            textColor = Color(0, 128, 128).toArgb()
            textSize = 11f
            this.typeface = typeface
            labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        }
    }
}
