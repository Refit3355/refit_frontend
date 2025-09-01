package com.refit.app.ui.composable.product.floating

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.refit.app.ui.theme.Pretendard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween

@Composable
fun SpeedDialMenu(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    items: List<SpeedDialItem>,
    endPadding: Dp = 16.dp,
    bottomPaddingFromFab: Dp = 96.dp,
    cardCorner: Dp = 24.dp,
    modifier: Modifier = Modifier,
) {
    if (!isOpen) return

    // 뒤로가기 닫기
    BackHandler(enabled = isOpen) { onDismiss() }

    // 최상단 레이어
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        // 스크림
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .noRippleClickable(onDismiss)
        )

        // 메뉴 카드 스택
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = endPadding, bottom = bottomPaddingFromFab),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            items.forEachIndexed { idx, item ->
                AnimatedVisibility(
                    visible = isOpen, // 메뉴 열림 상태에 따라 표시
                    enter = fadeIn(animationSpec = tween(180, delayMillis = idx * 40)) +
                            slideInVertically { it / 3 },
                    exit = fadeOut(animationSpec = tween(120)) +
                            slideOutVertically { it / 3 },
                    label = "speedDialItem$idx"
                ) {
                    MenuCard(
                        iconRes = item.iconRes,
                        iconTint = item.iconTint,
                        text = item.label,
                        corner = cardCorner,
                        onClick = {
                            onDismiss()
                            item.onClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuCard(
    iconRes: Int,
    text: String,
    iconTint: Color = Color.Unspecified,
    corner: Dp = 24.dp,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(corner),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .widthIn(min = 150.dp)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .noRippleClickable(onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF222222)
                )
            )
        }
    }
}

@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) { onClick() }
