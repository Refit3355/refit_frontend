package com.refit.app.ui.composable.mypage

import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.refit.app.R
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.me.modelAndView.OrderViewModel
import com.refit.app.util.order.OrderStatusMapper


@Composable
fun RecentOrderSection(
    order: OrderResponse,
    onClickAll: () -> Unit,
    vm: OrderViewModel,
    cartVm: CartEditViewModel,
) {
    val context = LocalContext.current

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
                            text = "주문번호 ${firstItem.orderCode}",
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
                                    text = OrderStatusMapper.getStatusText(item.status),
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
                                if (item.status == 1) {
                                    var showCancelDialog by remember { mutableStateOf(false) }

                                    if (showCancelDialog) {
                                        // 남은 취소 가능 수량 계산
                                        // item.canceledCount 가 없다면 아래 remainQty는 item.quantity 로 둠
                                        val remainQty = run {
                                            val canceled = try {
                                                @Suppress("UNUSED_VARIABLE")
                                                (item::class.java.getDeclaredField("canceledCount")
                                                    .apply { isAccessible = true }
                                                    .get(item) as? Int) ?: 0
                                            } catch (_: Exception) {
                                                0
                                            }
                                            val q = item.quantity - canceled
                                            if (q < 1) 1 else q
                                        }

                                        CancelOrderReasonDialog(
                                            orderItemId = item.orderItemId,
                                            unitPrice = item.price,
                                            maxQty = remainQty,
                                            onDismiss = { showCancelDialog = false },
                                            onConfirmCancel = { id, reason, count ->
                                                //  cancelAmount = unitPrice * count 으로 요청
                                                vm.requestCancel(
                                                    orderItemId = id,
                                                    unitPrice = item.price,
                                                    count = count,
                                                    reason = reason
                                                )
                                            }
                                        )
                                    }

                                    MyOrderActionButton(
                                        text = "주문 취소",
                                        modifier = Modifier
                                            .width(90.dp)
                                            .height(30.dp)
                                            .padding(top = 6.dp),
                                        onClick = { showCancelDialog = true }
                                    )
                                }

                                // 배송완료 → 교환/반품 신청 버튼
                                if (item.status == 6) {
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

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .clickable {
                                    cartVm.addOne(item.productId, 1)

                                    val inflater = LayoutInflater.from(context)
                                    val layout = inflater.inflate(R.layout.custom_toast, null)

                                    val textView = layout.findViewById<TextView>(R.id.toastText)
                                    textView.text = "${item.productName}이 장바구니에 추가되었습니다."

                                    Toast(context).apply {
                                        duration = Toast.LENGTH_SHORT
                                        view = layout
                                        show()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_icon_bag),
                                contentDescription = "장바구니 담기",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
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
