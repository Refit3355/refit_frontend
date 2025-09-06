package com.refit.app.ui.composable.health

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.refit.app.ui.theme.Pretendard

@Composable
fun ChartHeader(iconRes: Int, text: AnnotatedString) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, shape = BubbleShape(24f))
                .background(color = Color.White, shape = BubbleShape(24f))
                .padding(12.dp)
        ) {
            Text(
                text = text,
                fontFamily = Pretendard,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color.Black
            )
        }
    }
}
