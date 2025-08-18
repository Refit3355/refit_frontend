package com.refit.app.ui.composable.productDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun DetailBottomBar(
    wished: Boolean,
    onToggleWish: () -> Unit,
    onAddCart: () -> Unit,
    onBuyNow: () -> Unit
) {

    Surface(
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 찜
            Column(
                modifier = Modifier
                    .width(60.dp)
                    .clickable(
                        indication = null, // 리플 제거
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onToggleWish() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        if (wished) R.drawable.ic_heart_red else R.drawable.ic_heart_empty
                    ),
                    contentDescription = "찜"
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "찜하기",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = if (wished) Color(0xFFEC1A47) else DarkBlack
                )
            }


            Spacer(Modifier.width(12.dp))

            // 장바구니 (아웃라인)
            OutlinedButton(
                onClick = onAddCart,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, MainPurple),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MainPurple
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    "장바구니",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(Modifier.width(12.dp))

            // 구매하기 (필드)
            Button(
                onClick = onBuyNow,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    "구매하기",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
