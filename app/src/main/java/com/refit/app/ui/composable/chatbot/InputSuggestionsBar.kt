package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple

@Composable
fun InputSuggestionsBar(
    suggestions: List<FaqEntry>,
    onPick: (FaqEntry) -> Unit
) {
    if (suggestions.isEmpty()) return

    LazyRow(
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        items(suggestions) { e ->
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White,
                border = BorderStroke(1.dp, MainPurple),
                modifier = Modifier.clickable { onPick(e) }
            ) {
                Text(
                    text = e.question,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MainPurple,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
