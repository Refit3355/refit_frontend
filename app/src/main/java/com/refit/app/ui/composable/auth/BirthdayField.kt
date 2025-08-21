package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayField(
    value: LocalDate?,
    onChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }
    val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
    val text = value?.format(formatter) ?: ""

    Column(modifier = modifier) {
        Text(text = "생년월일", fontSize = 18.sp, style = MaterialTheme.typography.labelMedium,)
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("yyyy-MM-dd") },
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
            Box(Modifier.padding(end = 6.dp)) {
                InlineActionButton(
                    text = "선택",
                    onClick = { open = true },
                )
            }
        }
    }

    if (open) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        onChange(LocalDate.ofEpochDay(millis / 86_400_000L))
                    }
                    open = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}