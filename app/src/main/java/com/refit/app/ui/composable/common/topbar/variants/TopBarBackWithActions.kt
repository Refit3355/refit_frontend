package com.refit.app.ui.composable.common.topbar.variants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.basic.model.AppBarConfig
import com.refit.app.ui.composable.common.topbar.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackWithActionsTopBar(config: AppBarConfig.BackWithActions) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        navigationIcon = { BackButton(config.onBack) },
        title = {
            // title 슬롯 자체를 중앙 정렬
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(config.title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        },
        actions = {
            if (config.showActions) {
                Row(
                    modifier = Modifier.padding(end = TopBarTokens.HPad),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(com.refit.app.R.drawable.ic_icon_alarm),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(TopBarTokens.Icon)
                            .clickable { config.onAlarmClick() }
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(com.refit.app.R.drawable.ic_icon_bag),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(TopBarTokens.Icon)
                            .clickable { config.onCartClick() }
                    )
                }
            }
        }
    )
}
