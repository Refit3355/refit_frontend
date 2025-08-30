package com.refit.app.ui.composable.common.topbar

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.R
import com.refit.app.data.local.cart.LocalCartCount
import com.refit.app.data.push.viewmodel.NotificationBadgeViewModel
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
internal fun BackButton(onBack: () -> Unit) {
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
internal fun LogoButton(onClick: () -> Unit) {
    Icon(
        painter = painterResource(R.drawable.ic_logo_text),
        contentDescription = "Re:fit",
        tint = Color.Unspecified,
        modifier = Modifier
            .size(TopBarTokens.Touch)
            .clickable { onClick() }
    )
}

@Composable
internal fun ActionsRowCompact(
    onAlarmClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val cartCount = LocalCartCount.current

    Box(
        modifier = Modifier
            .width(TopBarTokens.SymWidth)
            .padding(end = TopBarTokens.HPad),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            BellIcon(onClick = onAlarmClick)

            Spacer(Modifier.width(8.dp))

            // 장바구니 아이콘 + 배지
            Box(
                modifier = Modifier
                    .size(TopBarTokens.Icon)
                    .clickable { onCartClick() }
                    .semantics {
                        contentDescription = if (cartCount > 0) {
                            "장바구니, 항목 $cartCount 개"
                        } else {
                            "장바구니, 비어 있음"
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_icon_bag),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.matchParentSize()
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = cartCount > 0,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    // 우상단 배지
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(18.dp)
                            .background(MainPurple, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartCount > 99) "99+" else cartCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.offset(y = (-3).dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun BellIcon(
    onClick: () -> Unit,
    vm: NotificationBadgeViewModel = viewModel()
) {
    val unread by vm.unread.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }

    Box(
        modifier = Modifier.size(28.dp).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_icon_alarm),
            contentDescription = "알림",
            tint = Color.Unspecified
        )

        if (unread > 0) {
            val textSize = if (unread < 10) 11.sp else 9.sp
            val textValue = if (unread > 99) "99" else unread.toString()

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textValue,
                    color = Color.White,
                    fontSize = textSize,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}