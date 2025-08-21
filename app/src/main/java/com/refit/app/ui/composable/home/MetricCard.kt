package com.refit.app.ui.composable.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.health.model.MetricItem
import com.refit.app.ui.theme.MainPurple

@Composable
fun MetricCard(item: MetricItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(120.dp)     // 카드 가로 크기
            .height(160.dp),   // 카드 세로 크기
        contentAlignment = Alignment.TopCenter
    ) {
        // 카드 본체
        Card(
            modifier = Modifier
                .matchParentSize()
                .clickable { item.onClick?.invoke() }
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 타이틀
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF6B6B6B)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )

                Spacer(Modifier.height(4.dp))

                // 값 + 단위
                Text(
                    text = "${item.value}${item.unit}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.value != "--") MainPurple
                        else Color(0xFF6B6B6B)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 아이콘
        Image(
            painter = painterResource(item.iconRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(90.dp)
                .offset(y = item.iconOffsetY.dp),
            contentScale = ContentScale.Fit
        )
    }
}
