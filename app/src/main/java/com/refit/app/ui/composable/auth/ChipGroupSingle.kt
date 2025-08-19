package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.refit.app.ui.composable.auth.SelectableChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroupSingle(
    options: List<String>,
    selected: String?,
    onChange: (String?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { opt ->
            SelectableChip(
                text = opt,
                selected = (selected == opt),
                onClick = { onChange(if (selected == opt) null else opt) }
            )
        }
    }
}