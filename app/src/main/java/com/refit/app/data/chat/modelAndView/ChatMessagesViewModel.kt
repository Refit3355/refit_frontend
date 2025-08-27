package com.refit.app.data.chat.modelAndView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.chat.model.ChatMessage
import com.refit.app.data.chat.usecase.GetChatMessagesPageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ChatMessagesUiState(
    val isInitialLoading: Boolean = false,
    val isLoadingOlder: Boolean = false,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList(), // 오래된 -> 최신순(오름차순)으로 유지
    val nextCursor: String? = null,
    val hasNext: Boolean = false
)

class ChatMessagesViewModel(
    private val getPage: GetChatMessagesPageUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ChatMessagesUiState())
        private set

    private var loadJob: Job? = null

    fun loadInitial(categoryId: Long, size: Int = 20) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            uiState = ChatMessagesUiState(isInitialLoading = true)
            runCatching { getPage(categoryId, size, null) }
                .onSuccess { page ->
                    // 서버는 최신->오래된(내림차순)일 가능성 높음 ⇒ 오름차순으로 뒤집어 보관
                    val ascending = page.items.sortedBy { it.createdAt }
                    uiState = uiState.copy(
                        isInitialLoading = false,
                        messages = ascending,
                        nextCursor = page.nextCursor,
                        hasNext = page.hasNext
                    )
                }
                .onFailure { e ->
                    uiState = uiState.copy(isInitialLoading = false, error = e.message)
                }
        }
    }

    fun loadOlder(categoryId: Long, size: Int = 20) {
        if (uiState.isInitialLoading || uiState.isLoadingOlder || !uiState.hasNext) return
        val cursor = uiState.nextCursor ?: return

        viewModelScope.launch {
            uiState = uiState.copy(isLoadingOlder = true)
            runCatching { getPage(categoryId, size, cursor) }
                .onSuccess { page ->
                    // 새로 받아온 "과거" 데이터를 오름차순으로 정렬 후 앞에 붙임
                    val newAsc = page.items.sortedBy { it.createdAt }
                    val merged = newAsc + uiState.messages
                    uiState = uiState.copy(
                        isLoadingOlder = false,
                        messages = merged,
                        nextCursor = page.nextCursor,
                        hasNext = page.hasNext
                    )
                }
                .onFailure { e ->
                    uiState = uiState.copy(isLoadingOlder = false, error = e.message)
                }
        }
    }
}
