package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple

@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            trailingIcon = trailing,
            colors = TextFieldDefaults.colors(
                // 내부 배경 흰색 고정
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                errorContainerColor = Color.White,

                // 테두리 색
                focusedIndicatorColor = MainPurple,
                unfocusedIndicatorColor = Color(0xFFE5E5EA),
                disabledIndicatorColor = Color(0xFFE5E5EA),
                errorIndicatorColor = Color.Red,

                // 텍스트 색
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,

                // 커서
                cursorColor = MainPurple,

                // 플레이스홀더 색
                focusedPlaceholderColor = Color(0xFF9E9E9E),
                unfocusedPlaceholderColor = Color(0xFF9E9E9E),
                disabledPlaceholderColor = Color.Gray,
                errorPlaceholderColor = Color.Red
            )
        )
    }
}
