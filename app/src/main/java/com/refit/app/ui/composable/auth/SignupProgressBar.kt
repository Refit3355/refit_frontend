package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupProgressBar(step: Int, total: Int = 3) {
    LinearProgressIndicator(
        progress = { step.toFloat() / total.toFloat() },
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF5C038C),
        trackColor = Color.LightGray
    )
}
