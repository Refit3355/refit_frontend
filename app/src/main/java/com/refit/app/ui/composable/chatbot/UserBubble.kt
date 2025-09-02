package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple

@Composable
fun UserBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            color = MainPurple,
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 8.dp,
                bottomStart = 16.dp, bottomEnd = 16.dp
            ),
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}