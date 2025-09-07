package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.Pretendard

@Composable
fun IngredientChip(text: String) {
    val labelStyle = LocalTextStyle.current.merge(
        TextStyle(
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 7.dp
            )
    ) {
        Text(
            text = text,
            style = labelStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
