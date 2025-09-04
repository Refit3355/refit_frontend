package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.refit.app.data.chatbot.modelAndView.ChatItem
import com.refit.app.data.chatbot.modelAndView.ChatbotViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.chatbot.*

@Composable
fun ChatbotScreen(
    navController: NavController,
    startTemplateId: String = "greeting",
    vm: ChatbotViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val nickname = remember { UserPrefs.getNickname() ?: "사용자" }

    // ViewModel의 StateFlow를 Compose 상태로 구독
    val messages by vm.messages.collectAsState()
    val convoVars by vm.vars.collectAsState()

    val listState = rememberLazyListState()
    var firstScrollDone by remember { mutableStateOf(false) }

    var input by remember { mutableStateOf(TextFieldValue("")) }
    var suggestions by remember { mutableStateOf(emptyList<FaqEntry>()) }

    // 최초 진입 보정
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) vm.reset(startTemplateId)
    }

    // 입력 디바운스
    LaunchedEffect(input.text) {
        kotlinx.coroutines.delay(150)
        suggestions = FaqIndex.suggest(input.text)
    }

    // 새 메시지 추가/키보드 뜰 때 하단 스크롤
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(messages.size, imeVisible) {
        if (messages.isNotEmpty()) {
            val base = messages.lastIndex
            val target = if (imeVisible) base + 1 else base
            listState.animateScrollToItem(target)
        }
    }
    LaunchedEffect(messages.size) {
        if (messages.isEmpty()) return@LaunchedEffect
        val lastIndex = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
        if (!firstScrollDone) {
            listState.scrollToItem(lastIndex)
            firstScrollDone = true
        } else {
            listState.animateScrollToItem(lastIndex)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        bottomBar = {
            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                InputSuggestionsBar(
                    suggestions = suggestions,
                    onPick = { e ->
                        vm.appendMessage(ChatItem.User(e.question))
                        vm.appendMessage(ChatItem.Bot(e.id))
                        input = TextFieldValue("")
                        suggestions = emptyList()
                    }
                )
                ChatBotInputBar(
                    value = input,
                    onValueChange = { input = it },
                    onSend = {
                        val q = input.text.trim()
                        if (q.isEmpty()) return@ChatBotInputBar
                        vm.appendMessage(ChatItem.User(q))
                        val hit = FaqIndex.suggest(q, topN = 1).firstOrNull()
                        vm.appendMessage(ChatItem.Bot(hit?.id ?: "faq_not_understood"))
                        input = TextFieldValue("")
                        suggestions = emptyList()
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color(0xFFFFE2EC),
                        0.50f to Color(0xFFFFFBE9),
                        0.75f to Color(0xFFF7FFF5),
                        1.00f to Color(0xFFE1F2F0)
                    )
                )
                .padding(12.dp)
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(messages.size) { i ->
                when (val item = messages[i]) {
                    is ChatItem.Bot -> {
                        BotTemplateBubble(
                            templateId = item.templateId,
                            variables   = mapOf("nickname" to nickname) + convoVars,
                            onUserReply = { userText -> vm.appendMessage(ChatItem.User(userText)) },
                            onNext      = { nextId   -> vm.appendMessage(ChatItem.Bot(nextId)) },
                            onSetVars   = { newVars  -> vm.setVars(newVars) },
                            onDeeplink  = { route ->
                                navController.navigate(route) { launchSingleTop = true }
                            }
                        )
                        // (Overview/FaqIndex 부가 버블은 기존과 동일)
                        if (item.templateId == "service_overview") {
                            Spacer(Modifier.height(10.dp))
                            OverviewCarouselMessage(
                                onNext = { next ->
                                    vm.appendMessage(ChatItem.Bot(next))
                                },
                                onUserReply = { label ->
                                    vm.appendMessage(ChatItem.User(label))
                                },
                                resetKey = i
                            )
                        }

                        if (item.templateId == "cosmetics_faq_index") {
                            Spacer(Modifier.height(10.dp))
                            FaqIndexMessage(
                                onPick = { entry ->
                                    vm.appendMessage(ChatItem.User(entry.question))
                                    vm.appendMessage(ChatItem.Bot(entry.id))
                                },
                                resetKey = i // 리스트 키 안정화
                            )
                        }
                        TimeStampKST(at = item.at, alignStart = true)
                    }
                    is ChatItem.User -> {
                        UserBubble(text = item.text)
                        TimeStampKST(at = item.at, alignStart = false)
                    }

                    else -> {}
                }
            }
        }
    }
}
