package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FaqIndexMessage(
    onPick: (FaqEntry) -> Unit,
    resetKey: Any
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 50.dp, end = 12.dp, bottom = 6.dp)
    ) {
        FaqIndexGridMessage(onPick = onPick, resetKey = resetKey)
    }
}
