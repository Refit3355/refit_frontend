package com.refit.app.ui.composable.productDetail

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.refit.app.ui.screen.formatWon
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    name: String,
    price: Int,
    originalPrice: Int,
    qty: Int,
    onQtyChange: (Int) -> Unit,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {

            Spacer(Modifier.height(12.dp))

            // 상품명
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            )
            Spacer(Modifier.height(14.dp))

            // 가격 + 수량 스테퍼
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatWon(price),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        if (originalPrice > price) {
                            Text(
                                text = formatWon(originalPrice),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.LineThrough,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                QuantityStepper(
                    value = qty,
                    onMinus = { onQtyChange(qty - 1) },
                    onPlus = { onQtyChange(qty + 1) }
                )
            }

            Spacer(Modifier.height(20.dp))

            // 확인 버튼
            Button(
                onClick = onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = primaryLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
