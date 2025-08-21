package com.refit.app.ui.composable.common

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.refit.app.ui.composable.model.basic.AppBarConfig

@Composable
fun appBarFor(route: String, nav: NavHostController): AppBarConfig {
    var query by rememberSaveable { mutableStateOf("") }

    val backStackEntry by nav.currentBackStackEntryAsState()
    val argQuery = backStackEntry?.arguments?.getString("query")
    LaunchedEffect(argQuery) {
        query = argQuery.orEmpty()      // ← 라우트 쿼리와 동기화
    }

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
                onClear = {
                    query = ""                // 로컬 상태도 비워주고
                    nav.navigate("search") {  // 쿼리 파라미터 없이 진입 → SearchScreen이 Suggest로 전환
                        launchSingleTop = true
                    }
                },
                showClear = true,
                onBack = { nav.popBackStack() }
            )

        route.startsWith("stepsDetail") ->
            AppBarConfig.BackWithActions(
                title = "걸음 수 리포트",
                onBack = { nav.popBackStack() },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick  = { nav.navigate("cart") },
                showActions = true
            )

        route.startsWith("sleepDetail") ->
            AppBarConfig.BackWithActions(
                title = "수면 리포트",
                onBack = { nav.popBackStack() },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick  = { nav.navigate("cart") },
                showActions = true
            )

        route.startsWith("weatherDetail") ->
            AppBarConfig.BackWithActions(
                title = "날씨 리포트",
                onBack = { nav.popBackStack() },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick  = { nav.navigate("cart") },
                showActions = true
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
