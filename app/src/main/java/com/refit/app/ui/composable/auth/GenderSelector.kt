package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refit.app.ui.viewmodel.auth.Gender

@Composable
fun GenderSelector(
    selected: Gender?,
    onSelect: (Gender) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == Gender.M,
            onClick = { onSelect(Gender.M) },
            label = { Text("남성") },
            colors = FilterChipDefaults.filterChipColors()
        )
        FilterChip(
            selected = selected == Gender.F,
            onClick = { onSelect(Gender.F) },
            label = { Text("여성") },
            colors = FilterChipDefaults.filterChipColors()
        )
    }
}