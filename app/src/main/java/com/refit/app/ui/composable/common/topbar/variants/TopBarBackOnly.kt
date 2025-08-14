package com.refit.app.ui.composable.common.topbar.variants

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.refit.app.ui.composable.model.basic.AppBarConfig
import com.refit.app.ui.composable.common.topbar.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackOnlyTopBar(config: AppBarConfig.BackOnly) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
        navigationIcon = { BackButton(config.onBack) },
        title = {
            Text(config.title, fontSize = 18.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    )
}
