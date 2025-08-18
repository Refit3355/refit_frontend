package com.refit.app.ui.composable.productDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Dimension
import coil.size.Size
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
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val context = LocalContext.current
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx().toInt() }

                // 세로는 대략 3배까지 허용(원하는 값으로 조절)
                val maxHeightPx = widthPx * 3

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(img.url)
                        // 화면 폭 기준으로 다운샘플링하도록 사이즈 지정
                        .size(Size(Dimension.Pixels(widthPx), Dimension.Pixels(maxHeightPx)))
                        // 너무 큰 하드웨어 비트맵으로 인한 텍스처 제한 회피 (필요할 때만)
                        .allowHardware(false)
                        // 메모리 절약(필요 시)
                        // .bitmapConfig(Bitmap.Config.RGB_565)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    loading = {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        Text(
                            "이미지를 불러오지 못했어요",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
