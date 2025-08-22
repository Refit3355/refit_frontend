package com.refit.app.ui.composable.cart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderBottomBar(
    total: Long,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val nf = remember { NumberFormat.getNumberInstance(Locale.KOREA) }

    Surface(shadowElevation = 8.dp,
        color = Color.White) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "${nf.format(total)}원 주문하기",
                fontSize = 17.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                color = Color.White)
        }
    }
}
