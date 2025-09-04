package com.refit.app.ui.composable.common.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.R
import com.refit.app.data.local.cart.LocalCartCount
import com.refit.app.data.push.viewmodel.NotificationBadgeViewModel
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

// 공통 배지 아이콘
@Composable
fun IconWithBadge(
    iconRes: Int,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    iconSize: Dp = 28.dp,
    badgeContainerColor: Color = MainPurple,
    badgeTextColor: Color = Color.White,
    showWhenZero: Boolean = false,
    cap: Int = 99,
    animate: Boolean = true,
    contentDescription: String? = null
) {
    val click by rememberUpdatedState(onClick)
    val visible = showWhenZero || count > 0
    val display = when {
        count > cap -> "$cap+"
        else -> count.toString()
    }
    val fontSize = if (count < 10) 11.sp else 9.sp

    BadgedBox(
        modifier = modifier
            .size(iconSize)
            .clickable { click() },
        badge = {
            if (animate) {
                AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
                    Badge(containerColor = badgeContainerColor, contentColor = badgeTextColor) {
                        Text(
                            text = if (showWhenZero && count == 0) "0" else display,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = fontSize,
                            maxLines = 1
                        )
                    }
                }
            } else if (visible) {
                Badge(containerColor = badgeContainerColor, contentColor = badgeTextColor) {
                    Text(
                        text = if (showWhenZero && count == 0) "0" else display,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        maxLines = 1
                    )
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = Color.Unspecified,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// 액션 영역 (알림 + 장바구니)
@Composable
fun ActionsRowCompact(
    onAlarmClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val cartCount = LocalCartCount.current

    Row(
        modifier = Modifier
            .width(TopBarTokens.SymWidth)
            .padding(end = TopBarTokens.HPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // 알림: 빨간색 배지 + 애니메이션
        BellIconVM(
            onClick = onAlarmClick
        )

        Spacer(Modifier.width(8.dp))

        // 장바구니: 보라색 배지 + 애니메이션
        IconWithBadge(
            iconRes = R.drawable.ic_icon_bag,
            count = cartCount,
            onClick = onCartClick,
            iconSize = TopBarTokens.Icon,
            badgeContainerColor = MainPurple,
            animate = true, // 명시적으로 true
            contentDescription = if (cartCount > 0) {
                "장바구니, 항목 $cartCount 개"
            } else {
                "장바구니, 비어 있음"
            }
        ).also {
            // 접근성 안내(개수/비어있음)를 주고 싶다면 외부 래핑으로 처리
        }
    }
}

// 알림 아이콘 (VM 포함)
// Stateless + VM 래퍼 분리. VM 쪽은 생명주기 안전 수집.
@Composable
fun BellIconVM(
    onClick: () -> Unit,
    vm: NotificationBadgeViewModel = viewModel()
) {
    val unread by vm.unread.collectAsStateWithLifecycle(initialValue = 0)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.refresh() // 화면 보일 때마다 최신 동기화
        }
    }
    IconWithBadge(
        iconRes = R.drawable.ic_icon_alarm,
        count = unread,
        onClick = onClick,
        iconSize = 28.dp,
        badgeContainerColor = Color(0xFFFF3B30), // 알림은 빨간 배지
        animate = true, // 명시적으로 true
        contentDescription = "알림"
    )
}

// 뒤로/로고 버튼
@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack, modifier = Modifier.size(TopBarTokens.Touch)) {
        Icon(
            painter = painterResource(R.drawable.ic_icon_back),
            contentDescription = "뒤로가기",
            tint = Color.Unspecified,
            modifier = Modifier.size(TopBarTokens.Icon)
        )
    }
}

@Composable
fun LogoButton(onClick: () -> Unit) {
    // 배지 없이 아이콘만 쓰려면 count=0, showWhenZero=false
    IconWithBadge(
        iconRes = R.drawable.ic_logo_text,
        count = 0,
        onClick = onClick,
        iconSize = TopBarTokens.Touch,
        showWhenZero = false,
        contentDescription = "Re:fit"
    )
}