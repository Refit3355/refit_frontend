package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.data.me.model.OrderResponse
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.me.modelAndView.OrderViewModel


@Composable
fun RecentOrderSection(
    order: OrderResponse,
    onClickAll: () -> Unit,
    vm: OrderViewModel,
    cartVm: CartEditViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightPurple)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 주문 내역",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Pretendard
            )
            TextButton(onClick = { onClickAll() }) {
                Text("전체보기", fontFamily = Pretendard, color = MainPurple)
            }
        }

        Spacer(Modifier.height(8.dp))

        // 주문 내역 카드 → 흰색
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                val firstItem = order.items.firstOrNull()
                if (firstItem != null) {
                    Column {
                        Text(
                            text = firstItem.createdAt.take(10).replace("-", "."),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Pretendard,
                            color = MainPurple
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "주문번호 ${firstItem.orderNumber}",
                            fontSize = 12.sp,
                            fontFamily = Pretendard,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.thumbnailUrl,
                                contentDescription = item.productName,
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = when (item.status) {
                                        0 -> "결제완료"
                                        1 -> "배송중"
                                        2 -> "배송완료"
                                        3 -> "취소완료"
                                        4 -> "교환 신청중"
                                        5 -> "교환 완료"
                                        6 -> "반품 신청중"
                                        7 -> "반품 완료"
                                        else -> "알수없음"
                                    },
                                    color = MainPurple,
                                    fontSize = 12.sp,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "[${item.brand}] ${item.productName}".limitWithEllipsis(10),
                                    fontSize = 14.sp,
                                    fontFamily = Pretendard,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row {
                                    Text(
                                        text = "${item.price}원",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Pretendard
                                    )
                                    if (item.originalPrice > item.price) {
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "${item.originalPrice}원",
                                            fontSize = 12.sp,
                                            fontFamily = Pretendard,
                                            color = Color.Gray,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "수량:${item.quantity}",
                                        fontSize = 12.sp,
                                        fontFamily = Pretendard,
                                        color = Color.Gray
                                    )
                                }

                                // 결제완료 → 주문취소 버튼
                                if (item.status == 0) {
                                    MyOrderActionButton(
                                        text = "주문 취소",
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(28.dp)
                                            .padding(top = 6.dp)
                                    )
                                }

                                // 배송완료 → 교환/반품 신청 버튼
                                if (item.status == 2) {
                                    var showDialog by remember { mutableStateOf(false) }

                                    if (showDialog) {
                                        ExchangeReturnReasonDialog(
                                            orderItemId = item.orderItemId,
                                            onDismiss = { showDialog = false },
                                            onConfirmExchange = { vm.requestExchange(it) },
                                            onConfirmReturn = { vm.requestReturn(it) }
                                        )
                                    }

                                    MyOrderActionButton(
                                        text = "교환/반품",
                                        modifier = Modifier
                                            .width(90.dp)
                                            .height(28.dp)
                                            .padding(top = 6.dp),
                                        onClick = { showDialog = true }
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                cartVm.addOne(item.productId, 1)
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "장바구니 담기",
                                tint = MainPurple
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun String.limitWithEllipsis(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}
