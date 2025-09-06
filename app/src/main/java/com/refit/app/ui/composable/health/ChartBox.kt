package com.refit.app.ui.composable.health

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ChartBox(
    height: Dp,
    visible: Boolean,
    onVisible: () -> Unit,
    content: (Context) -> android.view.View
) {
    val ctx = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .onGloballyPositioned { coords ->
                val y = coords.positionInWindow().y
                val screenHeight = ctx.resources.displayMetrics.heightPixels
                if (!visible && y in 0f..screenHeight.toFloat()) {
                    onVisible()
                }
            }
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(700))
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(height),
                factory = { context -> content(context) }
            )
        }
    }
}
