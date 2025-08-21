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

@Composable
fun CartItemRow(
    item: CartItemDto,
    checked: Boolean,
    onCheckChanged: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {
    val Purple = Color(0xFF5B0BB5)
    val Stroke = Color(0xFFE5E5EA)
    val SubGray = Color(0xFF9E9EA7)

    val hasDiscount = (item.discountRate ?: 0) > 0
    val unitPrice   = if (hasDiscount) item.discountedPrice else item.price
    val lineTotal   = unitPrice.toLong() * item.cartCnt

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
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {

            // ① 체크박스
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onCheckChanged() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        if (checked) R.drawable.ic_icon_checked else R.drawable.ic_icon_unchecked
                    ),
                    contentDescription = "선택",
                    tint = Purple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            // ② 상품 이미지
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Stroke, RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(10.dp))

            // ③ 우측 컨텐츠 영역
            Column(modifier = Modifier.weight(1f)) {

                // ─ 상단 행: 브랜드명(좌) - X 아이콘(우)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.brandName,
                        color = SubGray,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        painter = painterResource(R.drawable.ic_icon_delete),
                        contentDescription = "삭제",
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onRemove() }
                    )
                }

                // ─ 중간: 상품명 + 가격(할인율/할인가/정가)
                Text(
                    item.productName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 2
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasDiscount) {
                        Text(
                            text = "${item.discountRate}%",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = "%,d원".format(unitPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (hasDiscount) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "%,d원".format(item.price),
                            color = SubGray,
                            fontSize = 10.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                // ─ 하단 행: 수량 컨트롤(좌) - 합계(우)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 수량 컨트롤
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, Stroke, RoundedCornerShape(10.dp))
                            .height(30.dp)
                    ) {
                        Text(
                            "−",
                            modifier = Modifier
                                .clickable { onMinus() }
                                .padding(horizontal = 10.dp),
                            fontSize = 14.sp
                        )
                        Text(
                            item.cartCnt.toString(),
                            modifier = Modifier.padding(horizontal = 10.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "+",
                            modifier = Modifier
                                .clickable { onPlus() }
                                .padding(horizontal = 10.dp),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // 합계
                    Text(
                        text = "%,d원".format(lineTotal),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
