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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.navigation.NavController
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.util.common.PriceUtil
import com.refit.app.util.order.OrderStatusMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OrderItemRow(
    item: OrderItemDto,
    vm: OrderViewModel,
    cartVm: CartEditViewModel,
    onCartChanged: () -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("product/${item.productId}")
                },
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
                Row {
                    Text(
                        text = PriceUtil.formatPrice(item.price.toLong()),
                        fontFamily = Pretendard,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alignByBaseline()
                    )
                    if (item.originalPrice > item.price) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = PriceUtil.formatPrice(item.originalPrice.toLong()),
                            fontFamily = Pretendard,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "| ${item.quantity}개",
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.alignByBaseline()
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

                    // 교환/반품 다이얼로그
                    if (showDialog) {
                        ExchangeReturnReasonDialog(
                            orderItemId = item.orderItemId,
                            onDismiss = { showDialog = false },
                            onConfirmExchange = { vm.requestExchange(it) },
                            onConfirmReturn = { vm.requestReturn(it) }
                        )
                    }

                    Row {
                        MyOrderActionButton(
                            text = "구매 확정",
                            modifier = Modifier
                                .width(90.dp)
                                .height(28.dp)
                                .padding(top = 6.dp, end = 8.dp),
                            onClick = { vm.confirmReceipt(item.orderItemId) }
                        )

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
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()

                            val result = snackbarHostState.showSnackbar(
                                message = "${item.productName}을 장바구니에 담았어요.",
                                actionLabel = "바로가기",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                navController.navigate("cart")
                            }
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
