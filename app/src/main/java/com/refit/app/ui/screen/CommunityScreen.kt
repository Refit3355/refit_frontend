package com.refit.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.chat.modelAndView.ChatRoomsViewModel
import com.refit.app.data.chat.modelAndView.ChatRoomsViewModelFactory
import com.refit.app.ui.composable.combiking.CombiKingSection
import com.refit.app.ui.composable.community.CategoryChips
import com.refit.app.ui.composable.community.ChatRoomList
import com.refit.app.ui.composable.community.CommunityCategory
import com.refit.app.ui.composable.community.CommunityStatusTabs
import com.refit.app.ui.composable.community.CommunityTab

@Composable
fun CommunityScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableStateOf(CommunityTab.CHAT) }
    var selectedCategory by rememberSaveable { mutableStateOf(CommunityCategory.ALL) }

    // VM 연결
    val vm: ChatRoomsViewModel = viewModel(factory = ChatRoomsViewModelFactory)
    val ui = vm.uiState

    // 카테고리 변경 또는 CHAT 탭으로 전환 시 로드
    LaunchedEffect(selectedTab, selectedCategory) {
        if (selectedTab == CommunityTab.CHAT) vm.load(selectedCategory)
    }

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
                when {
                    ui.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    ui.error != null -> Text(
                        text = "불러오기에 실패했습니다: ${ui.error}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> ChatRoomList(
                        rooms = ui.rooms,
                        onClick = { room ->
                            navController.navigate("chat/${room.categoryId}")
                        }
                    )
                }
            }
            CommunityTab.COMBI -> CombiKingSection(navController, selectedCategory)
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