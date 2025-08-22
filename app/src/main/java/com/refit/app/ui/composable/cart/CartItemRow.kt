package com.refit.app.ui.composable.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.R
import com.refit.app.data.cart.model.CartItemDto
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CartItemRow(
    item: CartItemDto,
    checked: Boolean,
    onCheckChanged: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit,
    onOpenDetail: () -> Unit
) {
    val Stroke = Color(0xFFE5E5EA)
    val SubGray = Color(0xFF9E9EA7)

    val hasDiscount = (item.discountRate ?: 0) > 0
    val unitPrice   = if (hasDiscount) item.discountedPrice else item.price
    val lineTotal   = unitPrice.toLong() * item.cartCnt

    val leftColWidth = 28.dp
    val leftGap = 10.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ───────── 좌측 고정 컬럼: 체크박스
            Box(
                modifier = Modifier
                    .width(leftColWidth)   // 고정 폭
                    .height(28.dp)         // 터치 영역
                    .clickable { onCheckChanged() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        if (checked) R.drawable.ic_icon_checked else R.drawable.ic_icon_unchecked
                    ),
                    contentDescription = "선택",
                    tint = MainPurple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(leftGap))

            // ───────── 우측 가변 컬럼: (상단 정보) + (하단 수량/합계)
            Column(modifier = Modifier.weight(1f)) {

                // 상단: 이미지 + (브랜드/상품명/가격) + 삭제
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // 이미지
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Stroke, RoundedCornerShape(10.dp))
                            .clickable { onOpenDetail() }
                    ) {
                        AsyncImage(
                            model = item.thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // 정보 영역 + 삭제
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenDetail() }
                            ) {
                                Text(
                                    item.brandName,
                                    color = SubGray,
                                    fontSize = 12.sp,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight(500)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    item.productName,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight(500),
                                    maxLines = 2
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Icon(
                                painter = painterResource(R.drawable.ic_icon_delete),
                                contentDescription = "삭제",
                                tint = Color(0xFFB0B0B0),
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onRemove() }
                            )
                        }

                        // 가격 영역
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (hasDiscount) {
                                Text(
                                    text = "${item.discountRate}%",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight(500),
                                    fontFamily = Pretendard,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(
                                text = "%,d원".format(unitPrice),
                                fontFamily = Pretendard,
                                fontWeight = FontWeight(500),
                                fontSize = 14.sp
                            )
                            if (hasDiscount) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "%,d원".format(item.price),
                                    color = SubGray,
                                    fontSize = 10.sp,
                                    textDecoration = TextDecoration.LineThrough,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight(500)
                                )
                            }
                        }
                    }
                }

                // 구분선
                Spacer(Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Stroke)
                )
                Spacer(Modifier.height(15.dp))

                // 하단: 수량 조절(좌) ─ 합계(우)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, Stroke, RoundedCornerShape(10.dp))
                            .height(32.dp)
                    ) {
                        Text(
                            "−",
                            modifier = Modifier
                                .clickable { onMinus() }
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            fontSize = 16.sp,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight(600)
                        )
                        Text(
                            item.cartCnt.toString(),
                            modifier = Modifier.padding(horizontal = 10.dp),
                            fontSize = 14.sp,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight(600)
                        )
                        Text(
                            "+",
                            modifier = Modifier
                                .clickable { onPlus() }
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            fontSize = 16.sp,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight(600)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "%,d원".format(lineTotal),
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(600),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
