package com.refit.app.data.push.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.push.bus.PushEvents
import com.refit.app.data.push.model.NotificationBus
import com.refit.app.data.push.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "PushBadgeVM"

enum class ErrorKind { Server, Client, Network, Timeout, Unknown }
data class ErrorUi(val kind: ErrorKind, val code: Int? = null)

class NotificationBadgeViewModel(
    private val repo: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _unread = MutableStateFlow(0)
    val unread: StateFlow<Int> = _unread

    private fun Throwable.toErrorUi(): ErrorUi = when (this) {
        is HttpException -> {
            val kind = if (code() in 500..599) ErrorKind.Server else ErrorKind.Client
            ErrorUi(kind = kind, code = code())
        }
        is UnknownHostException -> ErrorUi(ErrorKind.Network)
        is SocketTimeoutException -> ErrorUi(ErrorKind.Timeout)
        else -> ErrorUi(ErrorKind.Unknown)
    }

    fun refresh() = viewModelScope.launch {
        runCatching { repo.getBadge().unreadCount }
            .onSuccess { count ->
                _unread.value = count
                // 최신값을 전역에도 반영(다른 화면/프로세스와 동기화)
                PushEvents.tryEmitBadge(count)
            }
            .onFailure { e -> Log.w(TAG, "badge refresh failed: ${e.toErrorUi()}", e) }
    }

    init {
        // 앱 시작/VM 초기 구동 시 1회 동기화
        viewModelScope.launch { refresh() }

        // FCM이 새 알림 이벤트를 던지면 서버에서 카운트 재조회
        viewModelScope.launch {
            NotificationBus.events
                .debounce(300)
                .collect { refresh() }
        }

        // 전역 배지 값 변경 스트림을 구독하여 UI 반영(FCM이 count를 직접 줬을 때 즉시 반영)
        viewModelScope.launch {
            PushEvents.badgeFlow
                .distinctUntilChanged()
                .collect { count -> _unread.value = count }
        }
    }

}
