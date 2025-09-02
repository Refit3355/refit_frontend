package com.refit.app.ui.composable.product.floating

import androidx.compose.ui.graphics.Color

data class SpeedDialItem(
    val label: String,
    val iconRes: Int,
    val iconTint: Color = Color.Unspecified,
    val onClick: () -> Unit
)
