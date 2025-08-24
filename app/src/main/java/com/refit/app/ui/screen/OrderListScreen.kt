package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.me.modelAndView.OrderViewModel
import com.refit.app.ui.composable.mypage.OrderItemRow
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun OrderListScreen(navController: NavController, vm: OrderViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadOrders()
    }

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        state.orders != null -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightPurple),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.orders!!.recentOrder) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val firstItem = order.items.firstOrNull()
                            if (firstItem != null) {
                                val date = firstItem.createdAt.take(10).replace("-", ".")
                                Text(
                                    text = date,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "주문번호 ${firstItem.orderNumber}",
                                    fontFamily = Pretendard,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(
                                    color = Color(0xFFE0E0E0),
                                    thickness = 1.dp
                                )
                                Spacer(Modifier.height(12.dp))
                            }

                            order.items.forEach { item ->
                                OrderItemRow(item, vm)
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
