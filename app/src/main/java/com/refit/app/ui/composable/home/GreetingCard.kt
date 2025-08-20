package com.refit.app.ui.composable.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import androidx.compose.foundation.border

@Composable
fun GreetingCard(nickname: String, tags: List<String>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프사
            Image(
                painter = painterResource(R.drawable.jellbbo_default),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, Color.Black, CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                // 닉네임 + 문구
                Text(
                    text = buildAnnotatedString {
                        // 닉네임 부분
                        withStyle(
                            style = SpanStyle(
                                color = MainPurple,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(nickname)
                        }

                        // 고정 문구 부분
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("님\n오늘 컨디션은 어떠신가요?")
                        }
                    },
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(8.dp))

                // 해시태그 칩
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.forEachIndexed { idx, t ->
                        HashtagChip(text = t)
                        if (idx != tags.lastIndex) Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}
