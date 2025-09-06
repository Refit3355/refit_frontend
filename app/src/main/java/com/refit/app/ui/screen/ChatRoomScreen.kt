package com.refit.app.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.chat.modelAndView.ChatMessagesViewModel
import com.refit.app.data.chat.modelAndView.ChatMessagesViewModelFactory
import com.refit.app.data.product.usecase.GetSearchProductsUseCase
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.community.chatRoom.ChatInputBar
import com.refit.app.ui.composable.community.chatRoom.ChatUiItem
import com.refit.app.ui.composable.community.chatRoom.DateChip
import com.refit.app.ui.composable.community.chatRoom.ListPage
import com.refit.app.ui.composable.community.chatRoom.ProductPickerBottomSheet
import com.refit.app.ui.composable.community.chatRoom.ProductShareBubble
import com.refit.app.ui.composable.community.chatRoom.ProductSummary
import com.refit.app.ui.composable.community.chatRoom.buildChatUiItems
import com.refit.app.ui.composable.community.chatRoom.koreanTime
import androidx.compose.runtime.withFrameNanos

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

    // 초기 로드 + 실시간 연결
    LaunchedEffect(categoryId) {
        vm.loadInitial(categoryId, size = 50)
        val wsUrl = "wss://api.refit.today/ws-stomp"
        vm.connectRealtime(categoryId, wsUrl)
    }
    DisposableEffect(Unit) { onDispose { vm.disconnectRealtime() } }

    // 최초 로드 완료 후 맨 아래로 이동
    var didScrollToBottom by remember { mutableStateOf(false) }
    LaunchedEffect(ui.isInitialLoading, ui.messages.size) {
        if (!ui.isInitialLoading && ui.messages.isNotEmpty() && !didScrollToBottom) {
            listState.scrollToItem(ui.messages.lastIndex)
            didScrollToBottom = true
        }
    }

    // 맨 위 닿으면 과거 로드
    val isAtTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
    }
    var prependInProgress by remember { mutableStateOf(false) }
    var sizeBeforePrepend by remember { mutableStateOf(0) }

    LaunchedEffect(isAtTop, ui.hasNext, ui.isInitialLoading, ui.isLoadingOlder) {
        if (isAtTop && ui.hasNext && !ui.isInitialLoading && !ui.isLoadingOlder) {
            prependInProgress = true
            sizeBeforePrepend = ui.messages.size
            vm.loadOlder(categoryId, size = 20)
        }
    }
    // 프리팬드 후 위치 보정
    LaunchedEffect(ui.messages.size) {
        if (prependInProgress) {
            val added = ui.messages.size - sizeBeforePrepend
            if (added > 0) {
                val currentIndex = listState.firstVisibleItemIndex + added
                val currentOffset = listState.firstVisibleItemScrollOffset
                listState.scrollToItem(currentIndex, currentOffset)
            }
            prependInProgress = false
        }
    }

    val myMemberId: Long? = remember { UserPrefs.getMemberId() }
    val uiItems = remember(ui.messages, myMemberId) {
        buildChatUiItems(ui.messages, myMemberId)
    }

    // IME(키보드) - 네비게이션바 차이만큼만 bottomBar에 패딩
    val density = LocalDensity.current
    val imePx = WindowInsets.ime.getBottom(density)
    val navPx = WindowInsets.navigationBars.getBottom(density)
    val effectiveImeDp = with(density) { (imePx - navPx).coerceAtLeast(0).toDp() }

    // 키보드가 뜨거나 새 아이템이 추가되면 하단으로 스크롤
    val isImeVisible = imePx > 0
    LaunchedEffect(isImeVisible, uiItems.size) {
        if (uiItems.isNotEmpty()) listState.animateScrollToItem(uiItems.lastIndex)
    }

    // 마지막 아이템 키(메시지 id 등) 추출
    val lastKey = remember(uiItems) {
        uiItems.lastOrNull()?.let {
            when (it) {
                is ChatUiItem.Message    -> "msg-${it.data.chatId}"
                is ChatUiItem.DateHeader -> "date-${it.date}"
            }
        }
    }

    // IME 변화 + 마지막 아이템 변화에 반응
    LaunchedEffect(imePx, lastKey) {
        if (uiItems.isNotEmpty()) {
            // IME/innerPadding 적용이 끝나도록 2프레임 대기
            withFrameNanos { }
            withFrameNanos { }
            listState.animateScrollToItem(uiItems.lastIndex)
        }
    }

    val focus = LocalFocusManager.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.White,
        bottomBar = {
            Column(
                Modifier
                    .padding(bottom = effectiveImeDp + 8.dp)
                    .fillMaxWidth()
            ) {
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
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            if (ui.isInitialLoading && ui.messages.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { detectTapGestures { focus.clearFocus() } },
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
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
                                    if (item.isMine) {
                                        com.refit.app.ui.composable.community.chatRoom.MyMessageBubble(msg)
                                    } else {
                                        com.refit.app.ui.composable.community.chatRoom.OtherMessageBubble(
                                            msg = msg,
                                            showAvatarAndName = item.showAvatarAndName
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showPicker) {
                ProductPickerBottomSheet(
                    onClose = { showPicker = false },
                    onSelect = { productId ->
                        showPicker = false
                        vm.sendRealtimeProduct(categoryId, productId)
                    },
                    loader = { q: String, cursor: String? ->
                        val res = searchUseCase(
                            query = q,
                            bhType = null,
                            cursor = cursor,
                            limit = 30,
                            sort = null
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
