package com.refit.app.ui.composable.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
){
    val bg = if(selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg = if(selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(24.dp),
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}