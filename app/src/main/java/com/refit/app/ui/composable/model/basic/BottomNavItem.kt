package com.refit.app.ui.composable.model.basic

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
)