package com.refit.app.ui.composable.community.chatRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.data.chat.model.ProductSnippet
import com.refit.app.ui.theme.Pretendard
import java.text.NumberFormat
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

@Composable
fun ProductCardContent(product: ProductSnippet?, nf: NumberFormat) {
    val headerStyle = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)
    )
    val brandStyle = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)
    )
    val nameStyle = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 18.sp,             // 한 줄 높이는 살짝 여유
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)
    )
    val priceStyle = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 18.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)
    )

    Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {

        // Header
        Text(
            text = "상품 공유",
            style = headerStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))

        // 얕은 구분선 (버블 패딩과 정렬)
        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            thickness = 0.8.dp
        )

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // 썸네일
            Box(
                modifier = Modifier
                    .size(64.dp) // 64 -> 56로 줄여 밀도 ↑
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                val thumb = product?.thumbnailUrl
                if (!thumb.isNullOrBlank()) {
                    AsyncImage(
                        model = thumb,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                // 브랜드
                Text(
                    text = product?.brandName ?: "브랜드",
                    style = brandStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // 상품명 (한 줄까지만, 말줄임)
                Text(
                    text = product?.productName ?: "상품 이름",
                    style = nameStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                // 가격/할인 (베이스라인 정렬로 단정하게)
                val discountRate = product?.discountRate ?: 0
                val price = product?.price ?: 0
                val discounted = product?.discountedPrice ?: price

                Row {
                    if (discountRate > 0) {
                        DiscountChip(
                            rate = discountRate,
                            modifier = Modifier.alignByBaseline()
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(
                        text = "${nf.format(discounted)}원",
                        style = priceStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
        }
    }
}