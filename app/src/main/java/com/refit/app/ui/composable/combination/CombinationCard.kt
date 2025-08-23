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
import com.refit.app.data.local.combination.model.CombinationResponse
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CombinationCard(
    combination: CombinationResponse,
    isSaved: Boolean,
    onToggleSave: (Long) -> Unit
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
                        text = combination.nickname.limitWithEllipsis(10),
                        fontSize = 14.sp,
                        fontFamily = Pretendard
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = combination.combinationName.limitWithEllipsis(10),
                    fontSize = 16.sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${combination.discountPrice}원",
                        fontSize = 16.sp,
                        color = MainPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${combination.originalPrice}원",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row {
                    val products = combination.products
                    val imageSize = 80.dp
                    val imageShape = RoundedCornerShape(6.dp)

                    val baseModifier = Modifier
                        .size(imageSize)

                    products.take(minOf(3, products.size)).forEachIndexed { index, product ->
                        val itemModifier =
                            if (index < 2 || products.size > 3) baseModifier.padding(end = 6.dp)
                            else baseModifier

                        Box(
                            modifier = itemModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = product.imageUrl,
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
                                    model = products[3].imageUrl,
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
                                    model = products[3].imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(imageShape)
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "저장수: ${combination.likes}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = { onToggleSave(combination.combinationId) }) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        tint = if (isSaved) MainPurple else Color.Gray,
                        contentDescription = if (isSaved) "저장됨" else "저장"
                    )
                }
            }
        }
    }
}

fun String.limitWithEllipsis(limit: Int): String {
    return if (this.length > limit) this.take(limit) + "..." else this
}
