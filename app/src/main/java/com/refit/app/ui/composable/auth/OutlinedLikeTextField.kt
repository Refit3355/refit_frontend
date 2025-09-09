package com.refit.app.ui.composable.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun OutlinedLikeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    shape: Shape = RoundedCornerShape(5.dp),
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    placeholderTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused = interaction.collectIsFocusedAsState().value

    val borderColor =
        if (!enabled) Color(0xFFE5E5EA)
        else if (focused) MainPurple
        else Color(0xFFE5E5EA)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = placeholderTextStyle.copy(fontFamily = Pretendard),
                color = Color(0xFF9E9E9E)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = { if (!readOnly && enabled) onValueChange(it) },
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            textStyle = textStyle.copy(
                color = if (enabled) Color.Black else Color.Gray,
                fontFamily = Pretendard
            ),
            cursorBrush = SolidColor(MainPurple),
            readOnly = readOnly,
            enabled = enabled,
            interactionSource = interaction,
            modifier = Modifier.fillMaxWidth()
        )
    }
}