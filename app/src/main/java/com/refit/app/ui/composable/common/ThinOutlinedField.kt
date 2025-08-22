package com.refit.app.ui.composable.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.GreyOutline
import com.refit.app.ui.theme.MainPurple

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor


@Composable
private fun ThinOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current
) {
    var focused by remember { mutableStateOf(false) }
    val borderC = when {
        !enabled -> GreyOutline
        focused -> MainPurple
        else -> GreyOutline
    }

    Box(
        modifier
            .border(1.dp, borderC, RoundedCornerShape(6.dp))
            .defaultMinSize(minHeight = 38.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .onFocusChanged { focused = it.isFocused },
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(placeholder, color = GreyOutline, style = textStyle)
        }
        BasicTextField(
            value = value,
            onValueChange = { if (enabled) onValueChange(it) },
            singleLine = true,
            textStyle = textStyle.copy(color = if (enabled) MaterialTheme.colorScheme.onSurface else GreyOutline),
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            cursorBrush = SolidColor(if (enabled) MainPurple else GreyOutline)
        )
    }
}
