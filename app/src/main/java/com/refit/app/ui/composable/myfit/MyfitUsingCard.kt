package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.data.myfit.model.MemberProductItem
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.GreyOutline
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.RedCaution

@Composable
fun MyfitUsingCard(
    item: MemberProductItem,
    onRecommend: () -> Unit,
    onClickItem: () -> Unit
) {

    val minCardHeight = 112.dp
    val thumbSize = 74.dp // 썸네일 고정 크기

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, GreyOutline),
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minCardHeight) // 카드 높이는 내용에 따라 늘어날 수 있음
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1) 썸네일
            MyfitThumb(
                imageUrl = item.thumbnailUrl,
                modifier = Modifier
                    .size(thumbSize)
            )

            Spacer(Modifier.width(16.dp))

            // 2) 텍스트/프로그레스 — Row 높이와 동일
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onClickItem() },
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 위쪽: 브랜드 + 상품명
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BrandPill(item.brandName)

                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkBlack,
                        maxLines = 2,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(2.dp))

                // 아래쪽: 프로그레스(+ 남은일) + (필요 시) 경고 문구
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val progress = run {
                        val total = (item.recommendedPeriod ?: 0).coerceAtLeast(0)
                        val remain = (item.daysRemaining ?: 0)
                        if (total <= 0) 0f else ((total - remain).toFloat() / total).coerceIn(0f, 1f)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MyfitProgressBar(
                            value = progress,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            item.displayRemaining.orEmpty(),
                            color = MainPurple,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    if ((item.daysRemaining ?: 0) < 0) {
                        OverdueHint(modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            // 3) 버튼 영역 — Row 높이와 동일, 가운데 정렬
            Box(
                Modifier
                    .fillMaxHeight()
                    .widthIn(min = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                RecommendCircleButton(onClick = onRecommend)
            }
        }
    }
}

@Composable
fun OverdueHint(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_exclamation),
            contentDescription = null,
            tint = RedCaution,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = "권장 사용기한 초과 - 교체 추천",
            style = MaterialTheme.typography.labelSmall,
            color = RedCaution,
            fontSize = 10.sp
        )
    }
}
