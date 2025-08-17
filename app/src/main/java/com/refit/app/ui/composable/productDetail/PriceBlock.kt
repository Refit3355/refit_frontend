package com.refit.app.ui.composable.productDetail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.product.model.ProductDetail
import com.refit.app.ui.screen.formatWon
import com.refit.app.ui.theme.Pretendard

@Composable
public fun PriceBlock(detail: ProductDetail) {
    val hasDiscount = detail.discountRate > 0 && detail.discountedPrice < detail.price

    if (hasDiscount) {
        Text(
            text = formatWon(detail.price),
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textDecoration = TextDecoration.LineThrough
        )

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${detail.discountRate}%",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = formatWon(detail.discountedPrice),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
        }
    } else {
        Text(
            text = formatWon(detail.price),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        )
    }
}