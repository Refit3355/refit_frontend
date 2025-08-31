package com.refit.app.ui.composable.mypage

import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.refit.app.R
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.data.me.model.OrderItemDto
import com.refit.app.data.me.modelAndView.OrderViewModel
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.util.order.OrderStatusMapper

@Composable
fun OrderItemRow(
    item: OrderItemDto,
    vm: OrderViewModel,
    cartVm: CartEditViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // 상태 텍스트
        val statusText = OrderStatusMapper.getStatusText(item.status)

        Text(
            text = statusText,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MainPurple,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 상품 이미지
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = item.productName,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                // [브랜드] 상품명
                Text(
                    text = "[${item.brand}] ${item.productName}",
                    fontFamily = Pretendard,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                // 가격/수량
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.originalPrice > item.price) {
                        Text(
                            text = "${item.originalPrice}원",
                            fontFamily = Pretendard,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = "${item.price}원",
                        fontFamily = Pretendard,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " | ${item.quantity}개",
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // 주문취소 버튼 (결제완료일 때만)
                if (item.status == 1) {
                    var showCancelDialog by remember { mutableStateOf(false) }

                    if (showCancelDialog) {
                        CancelOrderReasonDialog(
                            orderItemId = item.orderItemId,
                            unitPrice = item.price,
                            maxQty = item.quantity,
                            onDismiss = { showCancelDialog = false },
                            onConfirmCancel = { id, reason, count ->
                                vm.requestCancel(id, item.price, count, reason)
                            }
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    MyOrderActionButton(
                        text = "주문 취소",
                        modifier = Modifier.width(80.dp).height(24.dp),
                        onClick = { showCancelDialog = true }
                    )
                }

                // 교환/반품 버튼 (배송완료일 때만)
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
                            .width(80.dp)
                            .height(24.dp),
                        onClick = { showDialog = true }
                    )
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
    }
}
