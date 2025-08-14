package com.refit.app.ui.composable.common.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.refit.app.R

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
    Box(
        modifier = Modifier
            .width(TopBarTokens.SymWidth)
            .padding(end = TopBarTokens.HPad),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_icon_alarm),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(TopBarTokens.Icon)
                    .clickable { onAlarmClick() }
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.ic_icon_bag),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(TopBarTokens.Icon)
                    .clickable { onCartClick() }
            )
        }
    }
}
