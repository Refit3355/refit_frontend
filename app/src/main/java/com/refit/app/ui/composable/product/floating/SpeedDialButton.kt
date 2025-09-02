package com.refit.app.ui.composable.product.floating

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpeedDialButton(
    isOpen: Boolean,
    onToggle: () -> Unit,
    iconRes: Int,                 // 닫혀있을 때 보여줄 아이콘
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    closedBgColor: Color = Color.White,
    openBgColor: Color = Color.Black,   // 열렸을 때 FAB 배경색
    closeIconTint: Color = Color.White, // 열렸을 때 X 아이콘 색
    closeIconSize: Dp = 28.dp           // X 아이콘 크기
) {
    val bg = if (isOpen) openBgColor else closedBgColor

    FloatingActionButton(
        onClick = onToggle,
        shape = CircleShape,
        containerColor = bg,
        elevation = FloatingActionButtonDefaults.elevation(6.dp, 8.dp),
        modifier = modifier.size(size)
    ) {
        Crossfade(targetState = isOpen, label = "fab-crossfade") { open ->
            if (open) {
                // 열림 상태: 동그라미 안에 X
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "메뉴 닫기",
                    tint = closeIconTint,
                    modifier = Modifier.size(closeIconSize)
                )
            } else {
                // 닫힘 상태: 챗봇 이미지
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = "메뉴 열기",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
