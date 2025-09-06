package com.refit.app.ui.composable.health

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.geometry.Size

class BubbleShape(private val cornerRadius: Float = 24f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(20f, 0f, size.width, size.height),
                    CornerRadius(cornerRadius, cornerRadius)
                )
            )
            val tailCenterY = size.height / 2f
            moveTo(20f, tailCenterY - 20f)
            lineTo(0f, tailCenterY)
            lineTo(20f, tailCenterY + 20f)
            close()
        }
        return Outline.Generic(path)
    }
}
