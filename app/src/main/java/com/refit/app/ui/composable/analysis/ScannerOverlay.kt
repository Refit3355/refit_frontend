package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun ScannerOverlay(wRatio: Float, hRatio: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val boxW = w * wRatio
        val boxH = h * hRatio
        val left = (w - boxW) / 2f
        val top = (h - boxH) / 2f
        val right = left + boxW
        val bottom = top + boxH

        drawRect(Color.Black.copy(alpha = 0.45f), size = size)
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(boxW, boxH),
            blendMode = BlendMode.Clear
        )

        val stroke = 10f
        val corner = 56f
        fun line(ax: Float, ay: Float, bx: Float, by: Float) =
            drawLine(Color.White, Offset(ax, ay), Offset(bx, by), strokeWidth = stroke, cap = StrokeCap.Round)

        line(left, top + corner, left, top)
        line(left, top, left + corner, top)

        line(right - corner, top, right, top)
        line(right, top, right, top + corner)

        line(left, bottom - corner, left, bottom)
        line(left, bottom, left + corner, bottom)

        line(right - corner, bottom, right, bottom)
        line(right, bottom - corner, right, bottom)
    }
}