package com.refit.app.data.push.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 알림/배지 관련 전역 이벤트 버스.
 * - badgeFlow: 최신 미읽음 개수 이벤트를 내보냅니다.
 */
object PushEvents {
    private val _badgeFlow = MutableSharedFlow<Int>(replay = 1) // 마지막 값 유지
    val badgeFlow = _badgeFlow.asSharedFlow()

    suspend fun emitBadge(count: Int) {
        _badgeFlow.emit(count)
    }

    fun tryEmitBadge(count: Int) {
        _badgeFlow.tryEmit(count)
    }
}