package com.refit.app.ui.composable.combiking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CombinationCard(
    combination: CombinationDto,
    isSaved: Boolean,
    onToggleSave: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                // 프로필 + 닉네임
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = combination.profileUrl,
                        contentDescription = "프로필",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(combination.nickname, fontSize = 14.sp, fontFamily = Pretendard)
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    combination.combinationName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Pretendard
                )

                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${combination.price}원",
                        fontSize = 16.sp,
                        color = MainPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${combination.originalPrice}원",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 상품 이미지
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val images = combination.images
                    val size = 75.dp
                    val shape = RoundedCornerShape(6.dp)

                    images.take(4).forEachIndexed { index, url ->
                        Box(
                            modifier = Modifier.size(size).clip(shape),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize().clip(shape)
                            )
                            if (index == 3 && images.size > 4) {
                                Box(
                                    Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.45f), shape)
                                )
                                Text(
                                    text = "+${images.size - 3}",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Pretendard
                                )
                            }
                        }
                    }
                }
            }

            // 저장 버튼
            Column(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        tint = if (isSaved) MainPurple else Color.Gray,
                        contentDescription = "저장",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "${combination.likes}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainPurple
                )
            }
        }
    }
}
