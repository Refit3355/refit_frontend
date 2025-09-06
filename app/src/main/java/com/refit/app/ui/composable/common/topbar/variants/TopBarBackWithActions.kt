package com.refit.app.ui.composable.common.topbar.variants

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.refit.app.data.basic.model.AppBarConfig
import com.refit.app.ui.composable.common.topbar.BackButton
import com.refit.app.ui.composable.common.topbar.ActionsRowCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackWithActionsTopBar(config: AppBarConfig.BackWithActions) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
        navigationIcon = { BackButton(config.onBack) },
        title = {
            Text(
                text = config.title,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            if (config.showActions) {
                ActionsRowCompact(
                    onAlarmClick = config.onAlarmClick,
                    onCartClick  = config.onCartClick
                )
            }
        }
    )
}
