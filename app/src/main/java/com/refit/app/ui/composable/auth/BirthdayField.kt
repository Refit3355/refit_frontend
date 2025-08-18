package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayField(
    value: LocalDate?,
    onChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }

    Column (modifier = modifier){
        Text(text = "생년월일", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                TextButton(onClick = {open = true}) {Text("선택")}
            },
            placeholder = {Text("yyyy-MM-dd")}
        )
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
                }) {Text("확인")}
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("취소") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}