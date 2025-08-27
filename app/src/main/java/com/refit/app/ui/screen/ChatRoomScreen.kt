package com.refit.app.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.chat.modelAndView.ChatMessagesViewModel
import com.refit.app.data.chat.modelAndView.ChatMessagesViewModelFactory
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.community.chatRoom.ChatUiItem
import com.refit.app.ui.composable.community.chatRoom.DateChip
import com.refit.app.ui.composable.community.chatRoom.MyMessageBubble
import com.refit.app.ui.composable.community.chatRoom.OtherMessageBubble
import com.refit.app.ui.composable.community.chatRoom.buildChatUiItems

@Composable
fun ChatRoomScreen(
    navController: NavController,
    categoryId: Long
) {
    val vm: ChatMessagesViewModel = viewModel(factory = ChatMessagesViewModelFactory)
    val ui = vm.uiState
    val listState = rememberLazyListState()
    val access  = TokenManager.getAccessToken()
    val refresh = TokenManager.getRefreshToken()
    val mid = UserPrefs.getMemberId()
    val nick = UserPrefs.getNickname()
    val health = UserPrefs.getHealth()

    fun mask(s: String?): String =
        when { s.isNullOrBlank() -> "null"; s.length <= 12 -> "****"
            else -> "${s.take(6)}...${s.takeLast(6)}" }

    Log.d("AuthDebug", "Access=${access}")
    Log.d("AuthDebug", "Refresh=${mask(refresh)}")
    Log.d("AuthDebug", "User=memberId=$mid, nickname=$nick, health=$health")
    // 초기 로드
    LaunchedEffect(categoryId) { vm.loadInitial(categoryId, size = 20) }

    // 초기 로드 완료 후 맨 아래로 이동(최신 메시지)
    var didScrollToBottom by remember { mutableStateOf(false) }
    LaunchedEffect(ui.isInitialLoading, ui.messages.size) {
        if (!ui.isInitialLoading && ui.messages.isNotEmpty() && !didScrollToBottom) {
            listState.scrollToItem(ui.messages.lastIndex)
            didScrollToBottom = true
        }
    }

    // 맨 위에 닿으면 과거 로드
    val isAtTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
    }

    // 프리팬드 시 스크롤 위치 보존
    var prependInProgress by remember { mutableStateOf(false) }
    var sizeBeforePrepend by remember { mutableStateOf(0) }


    val myMemberId: Long? = remember { UserPrefs.getMemberId() }

    val uiItems = remember(ui.messages, myMemberId) {
        buildChatUiItems(ui.messages, myMemberId) // isMine 여부에 따라 My/Other 말풍선 렌더
    }

    LaunchedEffect(isAtTop, ui.hasNext, ui.isInitialLoading, ui.isLoadingOlder) {
        if (isAtTop && ui.hasNext && !ui.isInitialLoading && !ui.isLoadingOlder) {
            prependInProgress = true
            sizeBeforePrepend = ui.messages.size
            vm.loadOlder(categoryId, size = 20)
        }
    }

    // ui.messages가 늘어났고, 방금 prepend였다면 위치 보정
    LaunchedEffect(ui.messages.size) {
        if (prependInProgress) {
            val added = ui.messages.size - sizeBeforePrepend
            if (added > 0) {
                // 기존 첫 가시 아이템을 같은 위치에 유지
                val currentIndex = listState.firstVisibleItemIndex + added
                val currentOffset = listState.firstVisibleItemScrollOffset
                listState.scrollToItem(currentIndex, currentOffset)
            }
            prependInProgress = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (ui.isInitialLoading && ui.messages.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = uiItems,
                    key = {
                        when (it) {
                            is ChatUiItem.DateHeader -> "date-${it.date}"
                            is ChatUiItem.Message    -> "msg-${it.data.chatId}"
                        }
                    }
                ) { item ->
                    when (item) {
                        is ChatUiItem.DateHeader -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DateChip(label = item.date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 EEEE", java.util.Locale.KOREAN)))
                            }
                        }
                        is ChatUiItem.Message -> {
                            if (item.isMine) {
                                MyMessageBubble(item.data)
                            } else {
                                OtherMessageBubble(
                                    msg = item.data,
                                    showAvatarAndName = item.showAvatarAndName
                                )
                            }
                        }
                    }
                }

                // 맨 위 과거 로딩 인디케이터 등 필요 시 유지
                if (ui.isLoadingOlder) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
