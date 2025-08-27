package com.refit.app.data.push.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.push.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            .onSuccess { _unread.value = it }
            .onFailure { e -> Log.w(TAG, "badge refresh failed: ${e.toErrorUi()}", e) }
    }
}
