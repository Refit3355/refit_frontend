package com.refit.app.ui.composable.combiking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchModeBottomSheet(
    currentMode: String,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

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
                text = "검색 모드 선택",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            Spacer(Modifier.height(4.dp))

            listOf("product" to "상품명", "combination" to "조합명").forEach { (value, label) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelected(value)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = Pretendard,
                            fontWeight = if (currentMode == value) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (currentMode == value) MainPurple else DarkBlack
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    RadioButton(
                        selected = currentMode == value,
                        onClick = {
                            onSelected(value)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
