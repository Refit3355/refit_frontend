package com.refit.app.ui.composable.common.topbar.variants

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.refit.app.ui.composable.model.basic.AppBarConfig
import com.refit.app.ui.composable.common.topbar.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTitleTopBar(config: AppBarConfig.HomeTitle) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        navigationIcon = {
            Box(
                modifier = Modifier
                    .width(TopBarTokens.SymWidth)
                    .padding(start = TopBarTokens.HPad),
                contentAlignment = Alignment.CenterStart
            ) { LogoButton(config.onLogoClick) }
        },
        title = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(config.title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        },
        actions = {
            if (config.showActions) {
                ActionsRowCompact(config.onAlarmClick, config.onCartClick)
            } else {
                Spacer(Modifier.width(TopBarTokens.SymWidth))
            }
        }
    )
}
