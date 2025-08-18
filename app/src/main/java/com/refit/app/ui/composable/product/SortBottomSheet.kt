package com.refit.app.ui.composable.product

import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    options: List<Pair<String, String>>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "정렬",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            Spacer(Modifier.height(4.dp))

            options.forEachIndexed { i, (label, _) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelected(i)
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = Pretendard,
                            fontWeight = if (i == selectedIndex) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (i == selectedIndex) MainPurple else DarkBlack
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    RadioButton(
                        selected = i == selectedIndex,
                        onClick = { onSelected(i) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
