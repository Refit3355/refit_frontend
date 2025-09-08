package com.refit.app.ui.composable.order

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.ui.theme.Pretendard
import java.text.DecimalFormat

data class CompleteItemUi(
    val productId: Long,
    val brand: String,
    val productName: String,
    val price: Long,
    val originalPrice: Long,
    val quantity: Int,
    val thumbnailUrl: String
)

@Composable
fun OrderItemMiniRow(
    item: CompleteItemUi,
    onClickProduct: ((Long) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.productName,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = item.productName,
                fontFamily = Pretendard,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))

            Row {
                val money = DecimalFormat("#,###")
                Text(
                    text = "${money.format(item.price)}원",
                    fontFamily = Pretendard,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (item.originalPrice > item.price) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${money.format(item.originalPrice)}원",
                        fontFamily = Pretendard,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "| ${item.quantity}개",
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
