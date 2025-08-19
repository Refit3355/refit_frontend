package com.refit.app.ui.composable.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard


@Composable
fun RecentChip(
    text: String,
    onClick: () -> Unit,   // 채우고+검색
    onRemove: () -> Unit,  // 삭제
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        tonalElevation = 1.dp,
        color = Color.White,
        border = BorderStroke(color = MainPurple, width = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp
                ),
                color = MainPurple,
                modifier = Modifier.clickable { onClick() } // 텍스트 클릭
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                painter = painterResource(R.drawable.ic_icon_close),
                contentDescription = "삭제",
                tint = MainPurple,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() } // X 클릭
            )
        }
    }
}
