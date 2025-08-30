package com.refit.app.data.chat.socket

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class StompClient(
    private val url: String,
    private val connectHeadersProvider: () -> Map<String, String> = { emptyMap() }
) : WebSocketListener() {

    private val okHttp = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var ws: WebSocket? = null

    private val _events = MutableSharedFlow<StompEvent>(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events: SharedFlow<StompEvent> = _events

    fun connect() {
        if (ws != null) return
        val req = Request.Builder().url(url).build()
        ws = okHttp.newWebSocket(req, this)
    }

    fun disconnect() {
        ws?.close(1000, "bye"); ws = null
    }

    /** --- STOMP 프레임 유틸 --- */
    private fun frame(cmd: String, headers: Map<String, String> = emptyMap(), body: String? = null): String {
        val sb = StringBuilder().apply {
            append(cmd).append('\n')
            headers.forEach { (k, v) -> append("$k:$v").append('\n') }
            append('\n')
            if (body != null) append(body)
            append('\u0000')
        }
        return sb.toString()
    }

    fun sendConnect() {
        val base = mapOf("accept-version" to "1.2", "heart-beat" to "10000,10000")
        val headers = base + connectHeadersProvider()
        ws?.send(frame("CONNECT", headers))
    }

    fun subscribe(destination: String, id: String = "sub-0") {
        ws?.send(frame("SUBSCRIBE", mapOf("id" to id, "destination" to destination)))
    }

    fun send(destination: String, bodyJson: String) {
        ws?.send(frame("SEND", mapOf("destination" to destination, "content-type" to "application/json"), bodyJson))
    }

    fun sendDisconnect() {
        ws?.send(frame("DISCONNECT"))
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        _events.tryEmit(StompEvent.Open)
        sendConnect()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // 여러 프레임이 한 번에 들어올 수 있으므로 NULL(\u0000)로 분리
        text.split('\u0000').forEach { raw ->
            if (raw.isBlank()) return@forEach
            val lines = raw.lines()
            val cmd = lines.firstOrNull()?.trim().orElse()
            val headerLines = lines.drop(1).takeWhile { it.isNotEmpty() }
            val body = lines.drop(1 + headerLines.size + 1).joinToString("\n")
            when (cmd) {
                "CONNECTED" -> _events.tryEmit(StompEvent.Connected)
                "MESSAGE"   -> _events.tryEmit(StompEvent.Message(body))
                "ERROR"     -> _events.tryEmit(StompEvent.Error(body))
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        onMessage(webSocket, bytes.utf8())
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        _events.tryEmit(StompEvent.Closed(code, reason))
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        _events.tryEmit(StompEvent.Failure(t))
    }

    private fun String?.orElse(default: String = "") = this ?: default
}

sealed class StompEvent {
    data object Open : StompEvent()
    data object Connected : StompEvent()
    data class Message(val body: String) : StompEvent()
    data class Error(val body: String) : StompEvent()
    data class Closed(val code: Int, val reason: String) : StompEvent()
    data class Failure(val throwable: Throwable) : StompEvent()
}
