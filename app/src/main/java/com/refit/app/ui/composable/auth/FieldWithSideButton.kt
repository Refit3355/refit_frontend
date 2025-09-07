package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val FieldHeight = 50.dp

@Composable
fun FieldWithSideButton(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    supportingText: (@Composable () -> Unit)? = null,
    buttonText: String = "",
    buttonEnabled: Boolean = false,
    onButtonClick: () -> Unit = {},
    textFieldModifier: Modifier = Modifier,   // ← 여길 쓰게 됨
    showButton: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    fieldModifier: Modifier = Modifier,       // (선택) 기존 유지
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedLikeTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                readOnly = readOnly,
                enabled = enabled,
                visualTransformation = visualTransformation,
                modifier = Modifier
                    .weight(1f)
                    .height(FieldHeight)
                    .then(textFieldModifier)     // ★ 추가: bringIntoView 등 외부에서 전달한 Modifier 적용
                    .then(fieldModifier)         // (선택) 기존 fieldModifier도 이어서 적용
            )

            if (showButton) {
                Spacer(Modifier.width(8.dp))
                InlineActionButton(
                    text = buttonText,
                    enabled = buttonEnabled,
                    onClick = onButtonClick,
                    modifier = Modifier
                        .height(FieldHeight)
                        .defaultMinSize(minWidth = 88.dp)
                )
            }
        }

        if (supportingText != null) {
            Spacer(Modifier.height(6.dp))
            supportingText()
        }
    }
}
