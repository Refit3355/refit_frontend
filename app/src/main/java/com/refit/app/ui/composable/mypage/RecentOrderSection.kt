package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.refit.app.R
import com.refit.app.data.cart.modelAndView.CartEditViewModel
import com.refit.app.data.me.modelAndView.OrderViewModel
import com.refit.app.util.common.PriceUtil
import com.refit.app.util.order.OrderStatusMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RecentOrderSection(
    order: OrderResponse?,
    onClickAll: () -> Unit,
    vm: OrderViewModel,
    cartVm: CartEditViewModel,
    onCartChanged: () -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_list),
                    contentDescription = "최근 주문 내역",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "최근 주문 내역",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Pretendard
                )
            }
            TextButton(
                onClick = { onClickAll() },
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "전체보기",
                        fontFamily = Pretendard,
                        color = MainPurple
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "전체보기 이동",
                        tint = MainPurple,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(start = 2.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                if (order == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jellbbo_default),
                            contentDescription = "주문 내역 없음",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            text = "아직 주문 내역이 없어요.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
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
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { navController.navigate("product/${item.productId}") },
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
                                        text = OrderStatusMapper.getStatusText(item.status.toInt()),
                                        color = MainPurple,
                                        fontSize = 12.sp,
                                        fontFamily = Pretendard,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.productName,
                                        fontSize = 14.sp,
                                        fontFamily = Pretendard,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row {
                                        Text(
                                            text = PriceUtil.formatPrice(item.unitPrice),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Pretendard,
                                            modifier = Modifier.alignByBaseline()
                                        )
                                        if (item.originalUnitPrice > item.unitPrice) {
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = PriceUtil.formatPrice(item.originalUnitPrice),
                                                fontSize = 12.sp,
                                                fontFamily = Pretendard,
                                                color = Color.Gray,
                                                textDecoration = TextDecoration.LineThrough,
                                                modifier = Modifier.alignByBaseline()
                                            )
                                        }
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "| ${item.quantity}개",
                                            fontSize = 12.sp,
                                            fontFamily = Pretendard,
                                            color = Color.Gray,
                                            modifier = Modifier.alignByBaseline()
                                        )
                                    }

                                    // 결제완료 → 주문취소 버튼
                                    if (item.status.toInt() == 1) {
                                        var showCancelDialog by remember { mutableStateOf(false) }

                                        if (showCancelDialog) {
                                            CancelOrderReasonDialog(
                                                orderItemId = item.orderItemId,
                                                unitPrice = item.unitPrice,
                                                maxQty = item.quantityRemaining.toInt().coerceAtLeast(0),
                                                originalMerchandiseTotal = order.originalMerchandiseTotal,   // 주문 당시 총액
                                                currentMerchandiseSubtotal = order.currentMerchandiseSubtotal, // 현재 남은 총액
                                                onDismiss = { showCancelDialog = false },
                                                onConfirmCancel = { id, reason, count ->
                                                    vm.requestCancel(
                                                        orderItemId = id,
                                                        unitPrice = item.unitPrice.toInt(),
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
                                    if (item.status.toInt() == 6) {
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

                            // 장바구니 담기
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
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun String.limitWithEllipsis(maxLength: Int): String =
    if (this.length > maxLength) this.take(maxLength) + "..." else this
