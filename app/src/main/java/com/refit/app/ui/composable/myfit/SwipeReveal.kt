package com.refit.app.ui.composable.myfit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.refit.app.ui.theme.MainPurple

@Composable
fun SwipeRevealBox(
    modifier: Modifier = Modifier,
    actionWidth: Dp = 185.dp,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val maxReveal = with(LocalDensity.current) { actionWidth.toPx() }
    var offset by remember { mutableStateOf(0f) }
    val animOffset by animateFloatAsState(offset, label = "reveal")

    // 스와이프 중엔 이 아이템을 리스트의 최상단 Z로 올림
    val z = if (animOffset != 0f) 10f else 0f

    Box(
        modifier
            .fillMaxWidth()
            .zIndex(z) // 최상단
            .graphicsLayer { clip = false } // 자식 클리핑 방지
            .pointerInput(maxReveal) {
                detectHorizontalDragGestures(
                    onDragEnd = { offset = if (offset < -maxReveal / 2) -maxReveal else 0f }
                ) { change, drag ->
                    change.consume(); offset = (offset + drag).coerceIn(-maxReveal, 0f)
                }
            }
    ) {
        Row(
            Modifier
                .matchParentSize()
                .background(
                    Color.White,
                    shape = RoundedCornerShape(12.dp))
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(label = "사용완료", icon = Icons.Default.Check) { onComplete(); offset = 0f }
            Spacer(Modifier.width(10.dp))

            ActionButton(label = "수정하기", icon = Icons.Default.Edit) { onEdit(); offset = 0f }
            Spacer(Modifier.width(10.dp))

            ActionButton(label = "삭제하기", icon = Icons.Default.Close,
                shape = RoundedCornerShape(7.dp)) { onDelete(); offset = 0f }
        }

        // 앞 컨텐츠: 카드 자체의 모양/테두리는 콘텐츠 쪽에서(Surface) 처리
        Box(
            Modifier
                .graphicsLayer {
                    translationX = animOffset
                    clip = false
                }
        ) {
            content()
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    shape: Shape = CircleShape,
    onClick: () -> Unit
) {
    Column(Modifier.clickable { onClick() }, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(32.dp).background(MainPurple, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = MainPurple, style = MaterialTheme.typography.labelSmall)
    }
}