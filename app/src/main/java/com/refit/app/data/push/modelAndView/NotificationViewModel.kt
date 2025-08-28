package com.refit.app.data.push.modelAndView

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.push.model.NotificationRow
import com.refit.app.data.push.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "PushVM"

data class NotificationUiState(
    val items: List<NotificationRow> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorUi? = null
)

enum class ErrorKind { Client, Server, Network, Timeout, Unknown }
data class ErrorUi(val kind: ErrorKind, val code: Int? = null)

class NotificationViewModel(
    private val repo: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(NotificationUiState(isLoading = false))
    val ui: StateFlow<NotificationUiState> = _ui

    private fun Throwable.toErrorUi(): ErrorUi = when (this) {
        is HttpException -> {
            val kind = if (code() in 500..599) ErrorKind.Server else ErrorKind.Client
            ErrorUi(kind = kind, code = code())
        }
        is UnknownHostException -> ErrorUi(ErrorKind.Network)
        is SocketTimeoutException -> ErrorUi(ErrorKind.Timeout)
        else -> ErrorUi(ErrorKind.Unknown)
    }

    // 목록만 새로고침 (UI에 ‘들어오기 직전’ 상태를 그대로 표시)
    fun refresh() = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = null) }
        runCatching {
            repo.getNotifications().items
        }.onSuccess { list ->
            _ui.update { it.copy(isLoading = false, items = list ?: emptyList()) }
        }.onFailure { e ->
            logHttp(e)
            _ui.update { it.copy(isLoading = false, error = e.toErrorUi()) }
        }
    }

    // 화면에 표시된 뒤 서버에만 전체 읽음 처리 (UI는 변화 없음)
    fun markAllReadSilently() = viewModelScope.launch {
        runCatching { repo.readAll() }
            .onFailure { e -> Log.e(TAG, "readAll failed (silently)", e) }
    }

    // 필요 시: 전체 읽음 처리 후 목록까지 다시 가져오기 (재진입 시 사용 등)
    fun markAllReadAndRefresh() = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = null) }
        runCatching {
            repo.readAll()
            repo.getNotifications().items
        }.onSuccess { list ->
            _ui.update { it.copy(isLoading = false, items = list ?: emptyList()) }
        }.onFailure { e ->
            logHttp(e)
            _ui.update { it.copy(isLoading = false, error = e.toErrorUi()) }
        }
    }

    private fun logHttp(e: Throwable) {
        when (e) {
            is HttpException -> {
                val code = e.code()
                val raw = e.response()?.errorBody()?.string()
                val serverMsg = try { JSONObject(raw ?: "").optString("message", null) } catch (_: Exception) { null }
                Log.e(TAG, "HTTP $code failed body=$raw msg=$serverMsg", e)
            }
            is UnknownHostException -> Log.e(TAG, "Network unreachable", e)
            is SocketTimeoutException -> Log.e(TAG, "Timeout", e)
            else -> Log.e(TAG, "Unknown error", e)
        }
    }
}
