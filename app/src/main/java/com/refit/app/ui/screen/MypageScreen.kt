package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.mypage.*
import com.refit.app.ui.fake.sampleRecentOrder

@Composable
fun MypageScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val nickname = UserPrefs.getNickname()
        val health = UserPrefs.getHealth()

        val allTags = health?.toTags().orEmpty()

        MypageProfileCard(
            nickname = nickname ?: "사용자",
            tags = allTags
        )

        Spacer(Modifier.height(12.dp))

        RecentOrderSection(
            order = sampleRecentOrder,
            onClickAll = {
            }
        )

        Spacer(Modifier.height(12.dp))

        MypageMenuSection(navController = navController)
    }
}
