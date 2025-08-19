package com.refit.app.ui.composable.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.refit.app.ui.screen.formatWon
import com.refit.app.ui.theme.Pretendard

@Composable
fun PopularRankItem(
    rank: Int,                 // 1 ~ 10
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // ── ProductCard와 동일 톤의 텍스트 스타일 세트 ──
    val rankStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold
    )
    val brandStyle = MaterialTheme.typography.labelMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium
    )
    val nameStyle = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    )
    val priceStrongStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight(500)
    )
    val priceStrikeStyle = MaterialTheme.typography.labelMedium.copy(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1) 등수
        Box(
            modifier = Modifier
                .width(30.dp)
                .padding(end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = rankStyle
            )
        }

        // 2) 썸네일
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFD5D2D2), RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(Modifier.width(12.dp))

        // 3) 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // 브랜드
            Text(
                text = product.brand,
                style = brandStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 상품명
            Text(
                text = product.name,
                style = nameStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // 가격: 원가(취소선) + 할인율/할인가
            val original = product.price
            val discounted = product.discountedPrice
            val hasDiscount = product.discountRate > 0 && discounted < original

            Row(verticalAlignment = Alignment.CenterVertically) {
                // 할인율
                if (hasDiscount) {
                    Text(
                        text = "${product.discountRate}%",
                        color = MaterialTheme.colorScheme.error,
                        style = priceStrongStyle
                    )
                    Spacer(Modifier.width(8.dp))
                }

                // 할인가 or 판매가
                Text(
                    text = formatWon(if (hasDiscount) discounted else original),
                    style = priceStrongStyle
                )

                // 원가(취소선)
                if (hasDiscount) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatWon(original),
                        style = priceStrikeStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}
