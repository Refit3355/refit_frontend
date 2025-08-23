package com.refit.app.ui.composable.combination

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
import com.refit.app.data.local.combination.model.CombinationDto
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CombinationCard(
    combination: CombinationDto,
    isSaved: Boolean,
    onToggleSave: (Long) -> Unit,
    showSaveButton: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F3FF)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = combination.profileUrl,
                        contentDescription = "프로필",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = combination.nickname ?: "알 수 없음",
                        fontSize = 14.sp,
                        fontFamily = Pretendard
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = combination.combinationName,
                    fontSize = 16.sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${combination.discountedTotalPrice}원",
                        fontSize = 16.sp,
                        color = MainPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${combination.originalTotalPrice}원",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row {
                    val products = combination.productImages
                    val imageSize = 80.dp
                    val imageShape = RoundedCornerShape(6.dp)

                    val baseModifier = Modifier.size(imageSize)

                    products.take(minOf(3, products.size)).forEachIndexed { index, url ->
                        val itemModifier =
                            if (index < 2 || products.size > 3) baseModifier.padding(end = 6.dp)
                            else baseModifier

                        Box(
                            modifier = itemModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(imageShape)
                            )
                        }
                    }

                    when {
                        products.size >= 5 -> {
                            Box(
                                modifier = baseModifier,
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = products[3],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(imageShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(imageShape)
                                        .background(Color.Black.copy(alpha = 0.45f))
                                )
                                Text(
                                    text = "+${products.size - 4}",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Pretendard
                                )
                            }
                        }
                        products.size == 4 -> {
                            Box(
                                modifier = baseModifier,
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = products[3],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(imageShape)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showSaveButton) {
                        IconButton(
                            onClick = { onToggleSave(combination.combinationId) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                tint = if (isSaved) MainPurple else Color.Gray,
                                contentDescription = if (isSaved) "저장됨" else "저장",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = "저장수: ${combination.likes ?: 0}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainPurple,
                    )
                }
            }
        }
    }
}
