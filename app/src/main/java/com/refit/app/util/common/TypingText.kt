package com.refit.app.util.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.delay

@Composable
fun TypingText(
    fullText: String,
    typingDelay: Long = 150L,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {
        text = ""
        fullText.forEachIndexed { index, _ ->
            text = fullText.substring(0, index + 1)
            delay(typingDelay)
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        ),
        color = Color.Gray,
        modifier = modifier
    )
}
