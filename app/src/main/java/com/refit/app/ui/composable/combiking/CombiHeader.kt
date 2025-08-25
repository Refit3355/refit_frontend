package com.refit.app.ui.composable.combiking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard

@Composable
fun CombiHeader(totalCount: Int, sortType: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "총 ${totalCount}개의 조합",
            fontSize = 14.sp,
            fontFamily = Pretendard
        )
        Text(
            text = sortType,
            fontSize = 14.sp,
            fontFamily = Pretendard
        )
    }
}
