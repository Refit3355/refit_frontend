package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.ui.fake.RecentOrderDummy
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun RecentOrderSection(order: RecentOrderDummy) {
    Column(Modifier.padding(horizontal = 16.dp)) {
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
            TextButton(onClick = { /* 전체보기 */ }) {
                Text("전체보기", fontFamily = Pretendard, color = MainPurple)
            }
        }

        Spacer(Modifier.height(8.dp))

        // 주문 카드
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                // 기존 Text 대신 Column 배치
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = order.date,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Pretendard,
                            color = MainPurple
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "주문번호 ${order.orderNumber}",
                            fontSize = 12.sp,
                            fontFamily = Pretendard,
                            color = Color.Gray
                        )
                    }

                    // 주문 상세 보기가 있는 경우
                    // Icon(Icons.Default.ArrowForwardIos, contentDescription = "상세보기", tint = Color.Gray)
                }

                Spacer(Modifier.height(8.dp))

                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.productName,
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.status,
                                    color = MainPurple,
                                    fontSize = 12.sp,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.productName,
                                    fontSize = 14.sp,
                                    fontFamily = Pretendard,
                                    maxLines = 1
                                )
                                Row {
                                    Text(
                                        text = "${item.price}원",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Pretendard
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "${item.originalPrice}원",
                                        fontSize = 12.sp,
                                        fontFamily = Pretendard,
                                        color = Color.Gray,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "수량:${item.quantity}",
                                        fontSize = 12.sp,
                                        fontFamily = Pretendard,
                                        color = Color.Gray
                                    )
                                }

                                if (item.status != "취소완료") {
                                    Row {
                                        TextButton(onClick = { }) {
                                            Text("주문취소", fontSize = 12.sp, fontFamily = Pretendard)
                                        }
                                        TextButton(onClick = { }) {
                                            Text("교환/반품 신청", fontSize = 12.sp, fontFamily = Pretendard)
                                        }
                                    }
                                }
                            }
                        }
                        IconButton(onClick = { }) {
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
    }
}
