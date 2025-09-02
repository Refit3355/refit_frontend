package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.chatbot.BotTemplateBubble
import com.refit.app.ui.composable.chatbot.OverviewCarouselMessage
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatbotScreen(
    navController: NavController,
    startTemplateId: String = "greeting", // 최초 진입 템플릿
    onDeeplink: (String) -> Unit = { url ->
        runCatching { navController.navigate(url) }
    }
) {
    val gradient = remember {
        Brush.verticalGradient(
            0.00f to Color(0xFFFFE2EC),
            0.50f to Color(0xFFFFFBE9),
            0.75f to Color(0xFFF7FFF5),
            1.00f to Color(0xFFE1F2F0)
        )
    }
    val nickname = remember { UserPrefs.getNickname() ?: "사용자" }
    val timeText = remember {
        val nowKorea = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        nowKorea.format(DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA))
    }

    // 템플릿 id 스택(말풍선 목록)
    var stack by rememberSaveable { mutableStateOf(listOf(startTemplateId)) }

    // 리스트 상태
    val listState = rememberLazyListState()
    var firstScrollDone by remember { mutableStateOf(false) }

    // 새 아이템이 추가될 때마다 아래로 스크롤
    LaunchedEffect(stack.size) {
        if (stack.isEmpty()) return@LaunchedEffect
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(12.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            items(stack.size) { i ->
                val id = stack[i]

                // 1) 템플릿 말풍선
                BotTemplateBubble(
                    templateId = id,
                    variables = mapOf("nickname" to nickname),
                    onNext = { nextId ->
                        // greeting 리셋 등 기존 로직 유지
                        if (nextId == "greeting") stack = listOf("greeting")
                        else stack = stack + nextId
                    },
                    onDeeplink = onDeeplink
                )

                // 2) service_overview일 때만 “별도 아이템”으로 캐러셀 추가
                if (id == "service_overview") {
                    Spacer(Modifier.height(10.dp))
                    OverviewCarouselMessage(
                        onNext = { next -> stack = stack + next },
                        resetKey = i
                    )
                }
                Text(
                    text = timeText,
                    color = Color(0x99000000),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 62.dp, top = 2.dp, bottom = 4.dp)
                )
            }
        }

        // TODO: 하단 입력창(텍스트필드/전송)
    }
}
