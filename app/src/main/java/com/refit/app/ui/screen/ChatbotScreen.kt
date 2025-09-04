package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.chatbot.BotTemplateBubble
import com.refit.app.ui.composable.chatbot.FaqEntry
import com.refit.app.ui.composable.chatbot.FaqIndex
import com.refit.app.ui.composable.chatbot.InputSuggestionsBar
import com.refit.app.ui.composable.chatbot.OverviewCarouselMessage
import com.refit.app.ui.composable.chatbot.TimeStampKST
import com.refit.app.ui.composable.chatbot.UserBubble
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.Scaffold
import com.refit.app.ui.composable.chatbot.ChatBotInputBar
import com.refit.app.ui.composable.chatbot.FaqIndexMessage


sealed class ChatItem {
    data class Bot(val templateId: String, val at: Long = System.currentTimeMillis()) : ChatItem()
    data class User(val text: String,      val at: Long = System.currentTimeMillis()) : ChatItem()
}

@Composable
fun ChatbotScreen(
    navController: NavController,
    startTemplateId: String = "greeting", // 최초 진입 템플릿
    onDeeplink: (String) -> Unit = { url ->
        runCatching { navController.navigate(url) }
    }
) {
    val nickname = remember { UserPrefs.getNickname() ?: "사용자" }

    // 템플릿 id 스택(말풍선 목록)
    val messages = remember { mutableStateListOf<ChatItem>(ChatItem.Bot(startTemplateId)) }

    // 리스트 상태
    val listState = rememberLazyListState()
    var firstScrollDone by remember { mutableStateOf(false) }

    // 입력값
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var suggestions by remember { mutableStateOf(emptyList<FaqEntry>()) }

    // ... ChatbotScreen 내부 상태들 아래에 추가
    val density = LocalDensity.current
    var bottomBarHeightPx by remember { mutableStateOf(0) }

    // IME 상태는 그대로 사용 가능
    val imeHeightPx = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeHeightPx > 0

    // 입력창 높이 만큼 + 여유 여백
    val inputBarHeight = 14.dp
    val extraGap = if (isImeVisible) 10.dp else 8.dp
    val bottomPaddingDp = with(density) { bottomBarHeightPx.toDp() } + extraGap

    var convoVars by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // 키보드가 뜨거나 새 메시지가 추가되면, 마지막(＋스페이서)까지 스크롤
    LaunchedEffect(isImeVisible, messages.size) {
        if (messages.isNotEmpty()) {
            val base = messages.lastIndex
            val targetIndex = if (isImeVisible) base + 1 else base  // 스페이서 1개 고려
            listState.animateScrollToItem(targetIndex)
        }
    }

    // 새 아이템이 추가될 때마다 아래로 스크롤
    LaunchedEffect(messages.size) {
        if (messages.isEmpty()) return@LaunchedEffect
        val lastIndex = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
        if (!firstScrollDone) {
            // 최초 진입은 즉시 이동(점프)
            listState.scrollToItem(lastIndex)
            firstScrollDone = true
        } else {
            // 이후에는 부드럽게 애니메이션
            listState.animateScrollToItem(lastIndex)
        }
    }

    // 타이핑 디바운스 후 추천 갱신
    LaunchedEffect(input.text) {
        kotlinx.coroutines.delay(150)
        suggestions = FaqIndex.suggest(input.text)
    }


    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        bottomBar = {
            // 입력창 블록 (추천칩 + 입력필드)
            Column(
                modifier = Modifier
                    // 네비게이션바 + IME 인셋을 모두 소비
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                InputSuggestionsBar(
                    suggestions = suggestions,
                    onPick = { e ->
                        messages += ChatItem.User(e.question)
                        messages += ChatItem.Bot(e.id)
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
                        messages += ChatItem.User(q)
                        val hit = FaqIndex.suggest(q, topN = 1).firstOrNull()
                        if (hit == null) {
                            // 이해하지 못했을 때 전용 응답
                            messages += ChatItem.Bot("faq_not_understood")
                        } else {
                            messages += ChatItem.Bot(hit.id)
                        }
                        input = TextFieldValue("")
                        suggestions = emptyList()
                    }
                )
            }
        }
    ) { innerPadding ->
        // 리스트는 Scaffold가 준 패딩을 그대로 적용
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
                .padding(innerPadding), // 하단바+IME 공간만큼 자동 확보
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(messages.size) { i ->
                when (val item = messages[i]) {
                    is ChatItem.Bot -> {
                        BotTemplateBubble(
                            templateId = item.templateId,
                            variables   = mapOf("nickname" to nickname) + convoVars,
                            onUserReply = { userText -> messages += ChatItem.User(userText) },
                            onNext      = { nextId -> messages += ChatItem.Bot(nextId) },
                            onSetVars   = { newVars -> convoVars = convoVars + newVars },
                            onDeeplink  = { route ->
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                        if (item.templateId == "service_overview") {
                            Spacer(Modifier.height(10.dp))
                            OverviewCarouselMessage(
                                onNext = { next -> messages += ChatItem.Bot(next) },
                                onUserReply = { label -> messages += ChatItem.User(label) },
                                resetKey = i
                            )
                        }
                        if (item.templateId == "cosmetics_faq_index") {
                            Spacer(Modifier.height(10.dp))
                            FaqIndexMessage(
                                onPick = { entry ->
                                    messages += ChatItem.User(entry.question)
                                    messages += ChatItem.Bot(entry.id)
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
                }
            }
        }
    }
}
