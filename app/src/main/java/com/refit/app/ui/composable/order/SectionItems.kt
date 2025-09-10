package com.refit.app.ui.composable.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.refit.app.data.order.model.OrderItemSummary
import com.refit.app.ui.screen.formatWon

@Composable
private fun SectionItems(items: List<OrderItemSummary>) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("주문 상품", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        items.forEach { it ->
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                AsyncImage(
                    model = it.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(it.productName, maxLines = 1)
                    Text(it.brandName, style = MaterialTheme.typography.bodySmall)

                    // 가격 영역
                    val hasDiscount = it.discountRate > 0 && it.originalPrice > it.price
                    if (hasDiscount) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatWon(it.originalPrice.toInt()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(Modifier.width(8.dp))
                            DiscountBadge(rate = it.discountRate)
                        }
                    }
                    Text(
                        text = formatWon(it.price.toInt()),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("${formatWon((it.price * it.quantity).toInt())} (${it.quantity}개)")
                }
            }
        }
    }
}

@Composable
private fun DiscountBadge(rate: Long) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp),
        tonalElevation = 0.dp
    ) {
        Text(
            "-${rate}%",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
