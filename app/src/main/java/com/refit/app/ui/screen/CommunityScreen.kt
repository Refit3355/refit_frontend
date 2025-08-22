package com.refit.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.refit.app.ui.composable.community.CategoryChips
import com.refit.app.ui.composable.community.CommunityCategory
import com.refit.app.ui.composable.community.CommunityStatusTabs
import com.refit.app.ui.composable.community.CommunityTab

@Composable
fun CommunityScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableStateOf(CommunityTab.CHAT) }
    var selectedCategory by rememberSaveable { mutableStateOf(CommunityCategory.ALL) }

    Column {
        // 상단 탭
        CommunityStatusTabs(selectedTab) { selectedTab = it }

        // 카테고리 칩
        CategoryChips(
            selected = selectedCategory,
            onSelect = { selectedCategory = it }
        )

        // 탭 + 카테고리에 따른 콘텐츠
        when (selectedTab) {
            CommunityTab.CHAT -> {
                when (selectedCategory) {
                    CommunityCategory.ALL -> Text("전체 그룹채팅")
                    CommunityCategory.BEAUTY -> Text("뷰티 그룹채팅")
                    CommunityCategory.HEALTH -> Text("헬스 그룹채팅")
                }
            }
            CommunityTab.COMBI -> {
                when (selectedCategory) {
                    CommunityCategory.ALL -> Text("전체 조합왕")
                    CommunityCategory.BEAUTY -> Text("뷰티 조합왕")
                    CommunityCategory.HEALTH -> Text("헬스 조합왕")
                }
            }
        }
    }
}

@Composable
private fun GroupChatSection(navController: NavController) {
    // TODO: 카테고리별 실시간 그룹채팅 리스트/입장 UI 구성
    androidx.compose.material3.Text(
        text = "그룹채팅 탭입니다.",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun CombiKingSection(navController: NavController) {
    // TODO: ‘조합왕’ 게시판/피드 UI 구성
    androidx.compose.material3.Text(
        text = "조합왕 탭입니다.",
        modifier = Modifier.padding(16.dp)
    )
}
