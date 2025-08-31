package com.refit.app.data.chat.modelAndView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.chat.model.ChatMessage
import com.refit.app.data.chat.repository.ChatSocketRepository
import com.refit.app.data.chat.repository.StompState
import com.refit.app.data.chat.usecase.GetChatMessagesPageUseCase
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

data class ChatMessagesUiState(
    val isInitialLoading: Boolean = false,
    val isLoadingOlder: Boolean = false,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false
)

class ChatMessagesViewModel(
    private val getPage: GetChatMessagesPageUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ChatMessagesUiState())
        private set

    private var loadJob: Job? = null
    private var socketRepo: ChatSocketRepository? = null

    // VM 외부에서 awaitConnected()로 사용할 불린 플로우
    private val _connected = MutableStateFlow(false)
    val connected = _connected.asStateFlow()

    private var currentCategoryId: Long? = null

    fun loadInitial(categoryId: Long, size: Int = 20) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            uiState = ChatMessagesUiState(isInitialLoading = true)
            runCatching { getPage(categoryId, size, null) }
                .onSuccess { page ->
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

    fun connectRealtime(categoryId: Long, wsUrl: String, force: Boolean = false) {
        // 카테고리가 바뀌면 재연결
        if (!force && socketRepo != null && currentCategoryId == categoryId) return
        if (socketRepo != null) {
            socketRepo?.disconnect()
            socketRepo = null
        }
        currentCategoryId = categoryId

        _connected.value = false

        socketRepo = ChatSocketRepository(
            wsUrl = wsUrl,
            connectHeadersProvider = {
                buildMap {
                    TokenManager.getAccessToken()?.let { put("Authorization", "Bearer $it") }
                    UserPrefs.getNickname()?.let { put("nickname", it) }
                    UserPrefs.getMemberId()?.let { put("memberId", it.toString()) }
                }
            }
        ).also { repo ->
            // 1) 수신 메시지 UI 반영
            viewModelScope.launch {
                repo.incoming.collect { msg ->
                    uiState = uiState.copy(messages = uiState.messages + msg)
                }
            }
            // 2) STOMP 상태 → CONNECTED일 때만 true로 전환
            viewModelScope.launch {
                repo.state.collect { st ->
                    _connected.value = (st == StompState.CONNECTED)
                }
            }
            // 3) 실제 연결 + 구독
            repo.connectAndSubscribe(categoryId)
        }
    }

    suspend fun awaitConnected(timeoutMs: Long = 5_000): Boolean =
        withTimeoutOrNull(timeoutMs) {
            connected.filter { it }.first()
            true
        } ?: false

    fun sendRealtime(categoryId: Long, text: String) {
        val memberId = UserPrefs.getMemberId() ?: return
        socketRepo?.sendMessage(categoryId, memberId, text)
    }

    fun sendRealtimeProduct(categoryId: Long, productId: Long) {
        val memberId = UserPrefs.getMemberId() ?: return
        socketRepo?.sendMessage(
            categoryId = categoryId,
            memberId = memberId,
            message = "[상품 공유]",
            productId = productId
        )
    }

    fun disconnectRealtime() {
        socketRepo?.disconnect()
        socketRepo = null
        currentCategoryId = null
        _connected.value = false
    }

    override fun onCleared() {
        disconnectRealtime()
        super.onCleared()
    }
}
