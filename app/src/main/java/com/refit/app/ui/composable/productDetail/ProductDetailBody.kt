package com.refit.app.ui.composable.productDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.data.product.model.ProductDetail
import com.refit.app.ui.screen.formatRecommended
import com.refit.app.ui.theme.Pretendard

@Composable
fun ProductDetailBody(
    detail: ProductDetail,
    contentPadding: PaddingValues
) {
    val bottom = contentPadding.calculateBottomPadding()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 0.dp,
            bottom = bottom + 16.dp
        )
    ) {
        // 1) 상단 썸네일(대표 이미지 1장)
        item {
            val hero = detail.thumbnailUrl
            AsyncImage(
                model = hero,
                contentDescription = detail.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(top = 12.dp),
            )
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 2) 브랜드 / 상품명
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = detail.brand,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = detail.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        // 3) 가격 블록
        item {
            Column(Modifier.padding(horizontal = 24.dp)) {
                PriceBlock(detail)
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // 4) 기본 정보
        item { Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }
        item {
            InfoRow(
                label = "배송정보",
                value = "7일 이내"
            )
        }
        item { Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }
        item {
            InfoRow(
                label = "권장 소비기한",
                value = formatRecommended(detail.recommendedPeriod) // "360" -> "12개월" 등
            )
        }
        item { Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }
        item { Spacer(Modifier.height(20.dp)) }

        item {
            Text(
                text = "상세 정보",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }

        items(detail.images.sortedBy { it.order }) { img ->
            AsyncImage(
                model = img.url,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
