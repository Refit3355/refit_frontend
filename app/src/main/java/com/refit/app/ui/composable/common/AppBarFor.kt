package com.refit.app.ui.composable.common

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.refit.app.ui.composable.model.basic.AppBarConfig

@Composable
fun appBarFor(route: String, nav: NavHostController): AppBarConfig {
    var query by rememberSaveable { mutableStateOf("") }

    return when {
        route.startsWith("home") || route.startsWith("category") ->
            AppBarConfig.HomeSearch(
                query = query,
                onSearchClick = { nav.navigate("search") },
                onLogoClick = { nav.navigate("home") },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick = { nav.navigate("cart") },
                showActions = true
            )

        route.startsWith("community") || route.startsWith("myfit") || route.startsWith("my") ->
            AppBarConfig.HomeTitle(
                title = when {
                    route.startsWith("community") -> "커뮤니티"
                    route.startsWith("myfit") -> "마이핏"
                    route.startsWith("my") -> "마이페이지"
                    else -> "Re:fit"
                },
                onLogoClick  = { nav.navigate("home") },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick  = { nav.navigate("cart") },
                showActions = true
            )

        route in listOf("notifications", "cart") ->
            AppBarConfig.BackOnly(
                title = if (route == "notifications") "알림함" else "장바구니",
                onBack = { nav.popBackStack() }
            )

        route.startsWith("search") ->
            AppBarConfig.SearchOnly(
                query = query,
                onQueryChange = { query = it },
                onSubmit = { q ->
                    val term = (q.ifBlank { query }).trim()
                    if (term.isNotEmpty()) {
                        val encoded = Uri.encode(term)
                        nav.navigate("search?query=$encoded") {
                            launchSingleTop = true    // 같은 화면 중복 쌓이지 않게
                        }
                    }
                },
                showClear = true,
                onBack = { nav.popBackStack() }
            )

        else ->
            AppBarConfig.BackWithActions(
                title = when {
                    route.startsWith("product") -> "상품 상세"
                    route.startsWith("wish") -> "찜 목록"
                    else -> "Re:fit"
                },
                onBack = { nav.popBackStack() },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick  = { nav.navigate("cart") },
                showActions = true
            )
    }
}
