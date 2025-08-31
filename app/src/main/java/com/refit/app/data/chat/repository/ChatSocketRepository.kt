package com.refit.app.data.chat.repository

import android.util.Log
import com.refit.app.data.chat.model.ChatMessage
import com.refit.app.data.chat.model.ChatMessageDto
import com.refit.app.data.chat.model.toDomain
import com.refit.app.data.chat.socket.StompClient
import com.refit.app.data.chat.socket.StompEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatSocketRepository(
    wsUrl: String,
    private val connectHeadersProvider: () -> Map<String, String>
) {
    private val TAG = "ChatWS"
    private val stomp = StompClient(wsUrl, connectHeadersProvider)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }

    private val _incoming = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    val incoming: SharedFlow<ChatMessage> = _incoming

    fun connectAndSubscribe(categoryId: Long) {
        Log.d(TAG, "CONNECT() 호출. 이후 STOMP CONNECT 프레임 전송 예정")
        stomp.connect()

        scope.launch {
            stomp.events.collect { ev ->
                when (ev) {
                    is StompEvent.Open -> {
                        Log.d(TAG, "WebSocket OPEN (핸드셰이크 완료)")
                    }
                    is StompEvent.Connected -> {
                        Log.d(TAG, "STOMP CONNECTED. SUBSCRIBE 요청")
                        val dest = "/topic/chat.$categoryId"
                        stomp.subscribe(dest)
                        Log.d(TAG, "SUBSCRIBED → $dest")
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
                    }
                    is StompEvent.Failure -> {
                        Log.e(TAG, "WebSocket FAILURE", ev.throwable)
                    }
                }
            }
        }
    }

    fun sendMessage(categoryId: Long, memberId: Long, message: String, productId: Long? = null) {
        val payload = OutgoingMessage(categoryId, memberId, productId, message)
        val body = json.encodeToString(payload)
        Log.d(TAG, "SEND → /app/chat/send, payload=$body")
        stomp.send("/app/chat/send", body)
    }

    fun disconnect() {
        Log.d(TAG, "DISCONNECT 전송 및 소켓 종료")
        stomp.sendDisconnect()
        stomp.disconnect()
    }
}

@Serializable
data class OutgoingMessage(
    val categoryId: Long,
    val memberId: Long,
    val productId: Long? = null,
    val message: String
)
