package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple

@Composable
fun InlineActionButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.height(35.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainPurple,
            contentColor = Color.White
        )
    ) { Text(text, style = MaterialTheme.typography.labelMedium) }
}