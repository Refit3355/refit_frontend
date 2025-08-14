package com.refit.app.ui.composable.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.refit.app.ui.composable.model.basic.AppBarConfig
import com.refit.app.ui.composable.common.topbar.variants.HomeSearchTopBar
import com.refit.app.ui.composable.common.topbar.variants.SearchOnlyTopBar
import com.refit.app.ui.composable.common.topbar.variants.HomeTitleTopBar
import com.refit.app.ui.composable.common.topbar.variants.BackWithActionsTopBar
import com.refit.app.ui.composable.common.topbar.variants.BackOnlyTopBar

@Composable
fun RefitTopBar(config: AppBarConfig) {
    when (config) {
        is AppBarConfig.HomeSearch       -> HomeSearchTopBar(config)
        is AppBarConfig.SearchOnly       -> SearchOnlyTopBar(config)
        is AppBarConfig.HomeTitle        -> HomeTitleTopBar(config)
        is AppBarConfig.BackWithActions  -> BackWithActionsTopBar(config)
        is AppBarConfig.BackOnly         -> BackOnlyTopBar(config)
    }
}
