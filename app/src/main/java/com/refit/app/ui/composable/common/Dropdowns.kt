package com.refit.app.ui.composable.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.data.myfit.model.MyfitCategory
import com.refit.app.ui.theme.GreyInput
import com.refit.app.ui.theme.MainPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    items: List<MyfitCategory>,
    value: MyfitCategory?,
    onValueChange: (MyfitCategory) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "카테고리 선택",
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value?.name ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            placeholder = { Text(placeholder, color = GreyInput) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .heightIn(min = 44.dp),
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MainPurple,
                unfocusedIndicatorColor = GreyInput,
                cursorColor = GreyInput,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = GreyInput,
                disabledTextColor = LocalContentColor.current
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            items.forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.name) },
                    onClick = {
                        onValueChange(c)
                        expanded = false
                    }
                )
            }
        }
    }
}
