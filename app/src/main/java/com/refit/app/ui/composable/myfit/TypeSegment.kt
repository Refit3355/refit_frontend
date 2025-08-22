package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.GreyInput
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Purple10

@Composable
fun TypeSegment(
    current: String,
    onChange: (String) -> Unit,
    spacing: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SegItem("beauty", "뷰티", current == "beauty") { onChange("beauty") }
        SegItem("health", "헬스", current == "health") { onChange("health") }
    }
}

@Composable
private fun RowScope.SegItem(
    key: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(5.dp)
    OutlinedButton(
        onClick = onClick,
        shape = shape,
        border = BorderStroke(1.dp, if (selected) MainPurple else GreyInput),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Purple10 else Color.White,
            contentColor   = if (selected) MainPurple else GreyInput
        ),
        modifier = Modifier.weight(1f)
    ) { Text(text, style = MaterialTheme.typography.bodyMedium) }
}