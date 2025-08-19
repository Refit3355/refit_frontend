package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    trailing: @Composable (() -> Unit)? = null,
    labelTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    placeholderTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Column(modifier = modifier) {
        Text(text = label, style = labelTextStyle)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            trailingIcon = trailing,
            textStyle = textStyle,
            placeholder = {
                Text(
                    text = placeholder,
                    style = placeholderTextStyle
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                errorContainerColor = Color.White,

                focusedIndicatorColor = MainPurple,
                unfocusedIndicatorColor = Color(0xFFE5E5EA),
                disabledIndicatorColor = Color(0xFFE5E5EA),
                errorIndicatorColor = Color.Red,

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,

                cursorColor = MainPurple,

                focusedPlaceholderColor = Color(0xFF9E9E9E),
                unfocusedPlaceholderColor = Color(0xFF9E9E9E),
                disabledPlaceholderColor = Color.Gray,
                errorPlaceholderColor = Color.Red
            )
        )
    }
}
