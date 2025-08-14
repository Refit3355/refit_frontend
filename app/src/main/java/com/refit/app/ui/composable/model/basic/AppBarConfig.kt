package com.refit.app.ui.composable.model.basic

import androidx.compose.runtime.Composable

sealed class AppBarConfig {

    /* 로고 + 검색창 + (알림/장바구니) */
    data class HomeSearch(
        val query: String,
        val onSearchClick: () -> Unit,
        val onLogoClick: () -> Unit,
        val onAlarmClick: () -> Unit,
        val onCartClick: () -> Unit,
        val showActions: Boolean = true
    ) : AppBarConfig()

    /* 로고 + 중앙 타이틀 + (알림/장바구니) */
    data class HomeTitle(
        val title: String,
        val onLogoClick: () -> Unit,
        val onAlarmClick: () -> Unit,
        val onCartClick: () -> Unit,
        val showActions: Boolean = true
    ) : AppBarConfig()

    /* 뒤로가기 + 중앙 타이틀 + (알림/장바구니) */
    data class BackWithActions(
        val title: String,
        val onBack: () -> Unit,
        val onAlarmClick: () -> Unit,
        val onCartClick: () -> Unit,
        val showActions: Boolean = true
    ) : AppBarConfig()

    /* back only */
    data class BackOnly(
        val title: String,
        val onBack: () -> Unit
    ) : AppBarConfig()

    /* 검색 화면 */
    data class SearchOnly(
        val query: String,
        val onQueryChange: (String) -> Unit,
        val onSubmit: (String) -> Unit = {},
        val placeholder: String = "검색어를 입력하세요",
        val showClear: Boolean = true,
        val onBack: () -> Unit,
    ) : AppBarConfig()

    data class Custom(
        val title: @Composable () -> Unit,
        val navIcon: (@Composable () -> Unit)? = null,
        val actions: (@Composable () -> Unit)? = null
    ) : AppBarConfig()
}
