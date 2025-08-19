package com.refit.app.ui.composable.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.R
import com.refit.app.data.product.model.Product
import com.refit.app.ui.theme.Pretendard
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    item: Product,
    modifier: Modifier = Modifier,
    wished: Boolean,
    onToggleWish: () -> Unit,
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
        Column(Modifier.padding(6.dp)) {
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

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onToggleWish() }
                ) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconRes = if (wished) R.drawable.ic_heart_red else R.drawable.ic_heart_empty
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "찜"
                        )
                    }
                }
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
