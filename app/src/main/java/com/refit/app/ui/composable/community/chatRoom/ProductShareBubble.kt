package com.refit.app.ui.composable.community.chatRoom

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.chat.model.ProductSnippet
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.Pretendard
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "ProductShareBubble"

@Composable
fun ProductShareBubble(
    product: ProductSnippet?,
    mine: Boolean,
    showAvatarAndName: Boolean,
    nickname: String,
    profileUrl: String?,
    timeText: String,
    onOpenProduct: (Long) -> Unit
) {
    val nf = remember { NumberFormat.getNumberInstance(Locale.KOREA) }

    // 디버깅용(필요 없으면 제거 가능)
    LaunchedEffect(product?.id) {
        Log.d(TAG, "product: $product")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = if (mine) Alignment.Bottom else Alignment.Top
    ) {
        if (mine) {
            // 내 버블: 왼쪽 여백(아바타 자리), 오른쪽 정렬(시간 ─ 버블)
            Spacer(Modifier.size(36.dp))
            Spacer(Modifier.weight(1f))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = timeText,
                    fontFamily = Pretendard,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(Modifier.width(6.dp))

                Surface(
                    color = Color(0xFFEEEEEE),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 16.dp, topEnd = 8.dp,
                        bottomStart = 16.dp, bottomEnd = 16.dp
                    ),
                    modifier = Modifier
                        .widthIn(max = 240.dp)
                        .clickable(enabled = product?.id != null) {
                            product?.id?.let(onOpenProduct)
                        }
                ) {
                    ProductCardContent(product, nf)
                }
            }
        } else {
            // 상대 버블: 아바타/닉네임(optional) + (버블 ─ 시간) + 오른쪽 여백
            if (showAvatarAndName) {
                Avatar(profileUrl = profileUrl)
            } else {
                Spacer(Modifier.size(42.dp))
            }
            Spacer(Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {
                if (showAvatarAndName) {
                    Text(
                        text = nickname,
                        fontFamily = Pretendard,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Surface(
                        color = LightPurple,
                        shape = RoundedCornerShape(
                            topStart = 8.dp, topEnd = 16.dp,
                            bottomEnd = 16.dp, bottomStart = 16.dp
                        ),
                        modifier = Modifier
                            .widthIn(max = 240.dp)
                            .clickable(enabled = product?.id != null) {
                                product?.id?.let(onOpenProduct)
                            }
                    ) {
                        ProductCardContent(product, nf)
                    }

                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = timeText,
                        fontFamily = Pretendard,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
        }
    }
}