package com.refit.app.ui.composable.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refit.app.data.cart.model.CartItemDto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartSummarySection(
    items: List<CartItemDto>,
    selected: Set<Long>,
    freeShipThreshold: Long,
    shippingFee: Long
) {
    val nf = remember { NumberFormat.getNumberInstance(Locale.KOREA) }

    val subTotal = items
        .filter { it.cartId in selected }
        .sumOf { it.discountedPrice.toLong() * it.cartCnt }

    val shipping = when {
        subTotal == 0L -> 0L
        subTotal >= freeShipThreshold -> 0L
        else -> shippingFee
    }
    val total = subTotal + shipping

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("최종 결제 금액", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("총 상품 금액", color = Color.Gray)
            Text("${nf.format(subTotal)}원", color = Color.Gray)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("배송비", color = Color.Gray)
            Text(if (shipping == 0L) "무료" else "${nf.format(shipping)}원", color = Color.Gray)
        }

        Spacer(Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF4F0FA)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("결제 예상 금액", fontWeight = FontWeight.SemiBold)
                Text(
                    "${nf.format(total)}원",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
