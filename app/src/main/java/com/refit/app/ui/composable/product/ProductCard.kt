package com.refit.app.ui.composable.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.data.product.model.Product
import com.refit.app.ui.theme.Pretendard
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    item: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val imageShape = RoundedCornerShape(12.dp)
    val hasDiscount = item.discountRate > 0 && item.discountedPrice < item.price

    val brandStyle = MaterialTheme.typography.labelMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium
    )
    val nameStyle = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium
    )
    val priceStrongStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold
    )
    val priceStrikeStyle = MaterialTheme.typography.labelMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium
    )

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(imageShape)
                    .border(1.dp, Color(0xFFD5D2D2), imageShape)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                item.brand,
                style = brandStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.name,
                style = nameStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(6.dp))

            if (hasDiscount) {
                Column {
                    // 위 줄: 정가(취소선)
                    Text(
                        formatWon(item.price),
                        style = priceStrikeStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(Modifier.height(2.dp))
                    // 아래 줄: 할인율 + 할인가
                    Row {
                        Text(
                            "${item.discountRate}%",
                            color = MaterialTheme.colorScheme.error,
                            style = priceStrongStyle
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            formatWon(item.discountedPrice),
                            style = priceStrongStyle
                        )
                    }
                }
            } else {
                Text(
                    formatWon(item.price),
                    style = priceStrongStyle
                )
            }
        }
    }
}

private fun formatWon(value: Int) =
    NumberFormat.getNumberInstance(Locale.KOREA).format(value) + "원"
