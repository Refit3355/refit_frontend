package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.composable.home.HashtagChip
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun MypageProfileCard(
    nickname: String,
    tags: List<String>,
    onEditClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 이미지
            Image(
                painter = painterResource(R.drawable.jellbbo_default),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, MainPurple, CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // 오른쪽 정보
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nickname,
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.forEachIndexed { idx, t ->
                        HashtagChip(text = t)
                        if (idx != tags.lastIndex) Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MainPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "내 타입 수정",
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
