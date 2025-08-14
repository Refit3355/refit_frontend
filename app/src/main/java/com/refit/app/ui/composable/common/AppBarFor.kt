package com.refit.app.ui.composable.common

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
                        // 필요에 맞게 결과 라우트로 이동 (예: searchResult)
                        // Uri.encode(term) 쓰면 공백/특수문자 안전
                        //nav.navigate("searchResult?query=${Uri.encode(term)}")
                    }
                },
                placeholder = "브랜드, 상품, 태그 검색",
                showClear = true,
                onBack = { nav.popBackStack() }
            )

        else ->
            AppBarConfig.BackWithActions(
                title = "상세",
                onBack = { nav.popBackStack() },
                onAlarmClick = { nav.navigate("notifications") },
                onCartClick  = { nav.navigate("cart") },
                showActions = true
            )
    }
}
