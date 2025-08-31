package com.refit.app.data.chat.repository

import android.util.Log
import com.refit.app.data.chat.model.ChatMessage
import com.refit.app.data.chat.model.ChatMessageDto
import com.refit.app.data.chat.model.toDomain
import com.refit.app.data.chat.socket.StompClient
import com.refit.app.data.chat.socket.StompEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.ArrayDeque

enum class StompState { DISCONNECTED, CONNECTING, SOCKET_OPEN, CONNECTED }

class ChatSocketRepository(
    wsUrl: String,
    private val connectHeadersProvider: () -> Map<String, String>
) {
    private val TAG = "ChatWS"
    private val stomp = StompClient(wsUrl, connectHeadersProvider)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    private val _incoming = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    val incoming: SharedFlow<ChatMessage> = _incoming

    // 연결 상태 StateFlow
    private val _state = MutableStateFlow(StompState.DISCONNECTED)
    val state: StateFlow<StompState> = _state.asStateFlow()

    // CONNECTED 이전 SEND를 임시 보관할 큐
    private val pendingSends = ArrayDeque<() -> Unit>()

    fun connectAndSubscribe(categoryId: Long) {
        Log.d(TAG, "CONNECT() 호출. 이후 STOMP CONNECT 프레임 전송 예정")
        _state.value = StompState.CONNECTING
        stomp.connect()

        scope.launch {
            stomp.events.collect { ev ->
                when (ev) {
                    is StompEvent.Open -> {
                        Log.d(TAG, "WebSocket OPEN (핸드셰이크 완료)")
                        _state.value = StompState.SOCKET_OPEN
                    }
                    is StompEvent.Connected -> {
                        Log.d(TAG, "STOMP CONNECTED. SUBSCRIBE 요청")
                        _state.value = StompState.CONNECTED
                        val dest = "/topic/chat.$categoryId"
                        stomp.subscribe(dest)
                        Log.d(TAG, "SUBSCRIBED → $dest")

                        // 대기 중이던 SEND를 한 번에 플러시
                        while (pendingSends.isNotEmpty()) pendingSends.removeFirst().invoke()
                    }
                    is StompEvent.Message -> {
                        Log.d(TAG, "MESSAGE 수신(raw) = ${ev.body.take(300)}")
                        runCatching {
                            val dto = json.decodeFromString<ChatMessageDto>(ev.body)
                            _incoming.emit(dto.toDomain())
                            Log.d(TAG, "MESSAGE 파싱 성공 → chatId=${dto.chatId}, memberId=${dto.memberId}, msg=${dto.message}")
                        }.onFailure {
                            Log.e(TAG, "MESSAGE 파싱 실패", it)
                        }
                    }
                    is StompEvent.Error -> {
                        Log.e(TAG, "STOMP ERROR body=${ev.body}")
                    }
                    is StompEvent.Closed -> {
                        Log.w(TAG, "WebSocket CLOSED code=${ev.code}, reason=${ev.reason}")
                        _state.value = StompState.DISCONNECTED
                    }
                    is StompEvent.Failure -> {
                        Log.e(TAG, "WebSocket FAILURE", ev.throwable)
                        _state.value = StompState.DISCONNECTED
                    }
                }
            }
        }
    }

    fun sendMessage(categoryId: Long, memberId: Long, message: String, productId: Long? = null) {
        val payload = OutgoingMessage(categoryId, memberId, productId, message)
        val body = json.encodeToString(payload)
        val block = {
            Log.d(TAG, "SEND → /app/chat/send, payload=$body")
            stomp.send("/app/chat/send", body)
        }

        // CONNECTED 이전에는 큐에 쌓아두고, CONNECTED 되면 자동 전송
        if (state.value == StompState.CONNECTED) block() else pendingSends.add(block)
    }

    fun disconnect() {
        Log.d(TAG, "DISCONNECT 전송 및 소켓 종료")
        try { stomp.sendDisconnect() } catch (_: Throwable) {}
        stomp.disconnect()
        _state.value = StompState.DISCONNECTED
        pendingSends.clear()
    }
}

@Serializable
data class OutgoingMessage(
    val categoryId: Long,
    val memberId: Long,
    val productId: Long? = null,
    val message: String
)
