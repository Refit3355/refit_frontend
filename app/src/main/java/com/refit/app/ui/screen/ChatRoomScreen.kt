package com.refit.app.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
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
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.community.chatRoom.ChatUiItem
import com.refit.app.ui.composable.community.chatRoom.DateChip
import com.refit.app.ui.composable.community.chatRoom.MyMessageBubble
import com.refit.app.ui.composable.community.chatRoom.OtherMessageBubble
import com.refit.app.ui.composable.community.chatRoom.buildChatUiItems
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import com.refit.app.data.product.usecase.GetSearchProductsUseCase
import com.refit.app.ui.composable.community.chatRoom.ChatInputBar
import com.refit.app.ui.composable.community.chatRoom.ListPage
import com.refit.app.ui.composable.community.chatRoom.ProductPickerBottomSheet
import com.refit.app.ui.composable.community.chatRoom.ProductShareBubble
import com.refit.app.ui.composable.community.chatRoom.ProductSummary
import com.refit.app.ui.composable.community.chatRoom.koreanTime

@Composable
fun ChatRoomScreen(
    navController: NavController,
    categoryId: Long
) {
    val vm: ChatMessagesViewModel = viewModel(factory = ChatMessagesViewModelFactory)
    val ui = vm.uiState
    val listState = rememberLazyListState()

    // 입력값 상태
    var input by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    // 상품 공유
    var showPicker by remember { mutableStateOf(false) }
    val searchUseCase = remember { GetSearchProductsUseCase() }

    // 초기 로드
    LaunchedEffect(categoryId) {
        vm.loadInitial(categoryId, size = 20)              // 1) 초기 HTTP 로드

        // 에뮬레이터 ← PC 서버에 연결
        // val wsUrl = "ws://10.0.2.2:8080/ws-stomp" // 로컬
        val wsUrl = "wss://api.refit.today/ws-stomp" // 서버
        vm.connectRealtime(categoryId, wsUrl)
    }

    DisposableEffect(Unit) {
        onDispose { vm.disconnectRealtime() }              // 3) 화면 떠날 때 종료
    }

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

    val density = LocalDensity.current
    val imeHeightPx = WindowInsets.ime.getBottom(density)  // 0보다 크면 보이는 중
    val isImeVisible = imeHeightPx > 0
    val inputBarHeight = 14.dp
    val extraGap = if (isImeVisible) 10.dp else 8.dp
    val focus = LocalFocusManager.current

    LaunchedEffect(isImeVisible, uiItems.size) {
        if (uiItems.isNotEmpty()) {
            val targetIndex = if (isImeVisible) uiItems.lastIndex + 1 else uiItems.lastIndex
            listState.animateScrollToItem(targetIndex)
        }
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
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            if (ui.isInitialLoading && ui.messages.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { focus.clearFocus() }) // 바깥 탭 → 포커스 해제
                        },
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    items(
                        items = uiItems,
                        key = {
                            when (it) {
                                is ChatUiItem.DateHeader -> "date-${it.date}"
                                is ChatUiItem.Message -> "msg-${it.data.chatId}"
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
                                    DateChip(
                                        label = item.date.format(
                                            java.time.format.DateTimeFormatter.ofPattern(
                                                "yyyy년 MM월 dd일 EEEE",
                                                java.util.Locale.KOREAN
                                            )
                                        )
                                    )
                                }
                            }

                            is ChatUiItem.Message -> {
                                val msg = item.data
                                if (msg.productId != null) {
                                    // 상품 공유 메시지: 상품 버블로 렌더
                                    ProductShareBubble(
                                        product = msg.product,
                                        mine = item.isMine,
                                        showAvatarAndName = item.showAvatarAndName,
                                        nickname = msg.nickname,
                                        profileUrl = msg.profileUrl,
                                        timeText = msg.createdAt.koreanTime(),
                                        onOpenProduct = { pid -> navController.navigate("product/$pid") }
                                    )
                                } else {
                                    // 텍스트 메시지 기존 렌더
                                    if (item.isMine) {
                                        MyMessageBubble(msg)
                                    } else {
                                        OtherMessageBubble(
                                            msg = msg,
                                            showAvatarAndName = item.showAvatarAndName
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isImeVisible) {
                        item(key = "bottom-spacer") {
                            Spacer(Modifier.height(inputBarHeight + extraGap))
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
                // 하단 입력창
                ChatInputBar(
                    value = input,
                    onValueChange = { input = it },
                    onSend = {
                        val text = input.text.trim()
                        if (text.isNotEmpty()) {
                            vm.sendRealtime(categoryId, text)
                            input = TextFieldValue("")
                        }
                    },
                    onPickProduct = { showPicker = true }
                )
                if (showPicker) {
                    ProductPickerBottomSheet(
                        onClose = { showPicker = false },
                        onSelect = { productId ->
                            showPicker = false
                            vm.sendRealtimeProduct(categoryId, productId)   // 상품 공유 WS 전송
                        },
                        loader = { q: String, cursor: String? ->
                            val res = searchUseCase(
                                query  = q,        // ""면 전체 조회
                                cursor = cursor,   // 다음 페이지 커서
                                limit  = 30,
                                sort   = null      // 정렬 없음
                            )
                            res.fold(
                                onSuccess = { page ->
                                    ListPage(
                                        items = page.items.map { p ->
                                            ProductSummary(
                                                id = p.id,
                                                productName = p.name,
                                                thumbnailUrl = p.image,
                                                price = formatWon(p.price),
                                                discountRate = p.discountRate,
                                                discountedPrice = p.discountedPrice
                                            )
                                        },
                                        nextCursor = page.nextCursor,
                                        hasNext = page.hasMore
                                    )
                                },
                                onFailure = { ListPage(emptyList(), nextCursor = null, hasNext = false) }
                            )
                        }
                    )
                }
            }
        }
    }
}
