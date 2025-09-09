package com.refit.app.ui.composable.auth

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayField(
    value: LocalDate?,
    onChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }
    val zone = remember { ZoneId.systemDefault() }
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val text = value?.format(formatter) ?: ""

    FieldWithSideButton(
        modifier = modifier,
        label = "생년월일",
        value = text,
        onValueChange = {},
        placeholder = "yyyy-MM-dd",
        readOnly = true,
        enabled = true,
        buttonText = "선택",
        buttonEnabled = true,
        onButtonClick = { open = true },
        showButton = true
    )

    if (open) {
        val initialMillis = remember(value, zone) {
            value?.atStartOfDay(zone)?.toInstant()?.toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val picked = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
                        onChange(picked)
                    }
                    open = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text("취소") }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    headlineContentColor = MaterialTheme.colorScheme.onSurface,
                    weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    dayContentColor = MaterialTheme.colorScheme.onSurface,
                    disabledDayContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
