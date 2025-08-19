package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.refit.app.ui.composable.auth.SelectableChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroupMulti(
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    exclusiveOption: String? = null,
    maxSelection: Int? = null
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { opt ->
            val isSelected = opt in selected
            val blockedByMax = maxSelection != null &&
                    !isSelected && selected.size >= maxSelection

            SelectableChip(
                text = opt,
                selected = isSelected,
                onClick = {
                    if (exclusiveOption != null && opt == exclusiveOption) {
                        if (!isSelected) {
                            selected.filter { it != exclusiveOption }
                                .forEach { onToggle(it) }
                        }
                        onToggle(opt)
                        return@SelectableChip
                    }
                    if (exclusiveOption != null && exclusiveOption in selected && opt != exclusiveOption) {
                        onToggle(exclusiveOption)
                    }
                    if (!blockedByMax) onToggle(opt)
                }
            )
        }
    }
}