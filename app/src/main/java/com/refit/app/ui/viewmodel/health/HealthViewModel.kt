package com.refit.app.ui.viewmodel.health

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.health.HealthRepo
import com.refit.app.model.health.DailyRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * HealthScreen 상태 관리 ViewModel
 */
class HealthViewModel : ViewModel() {

    // UI 상태 데이터 클래스
    data class UiState(
        val days: Int = 7,
        val rows: List<DailyRow> = emptyList(),
        val loading: Boolean = false,
        val error: String? = null,
        val permissionGranted: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // 권한 허용 후 호출
    fun onPermissionGranted(ctx: Context) {
        _uiState.value = _uiState.value.copy(permissionGranted = true)
        fetch(ctx)
    }

    // 조회 기간 변경
    fun onDaysChanged(ctx: Context, days: Int) {
        _uiState.value = _uiState.value.copy(days = days)
        if (_uiState.value.permissionGranted && !_uiState.value.loading) {
            fetch(ctx)
        }
    }

    // Health 데이터 가져오기
    fun fetch(ctx: Context) {
        if (_uiState.value.loading) return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = null)
                val result = HealthRepo.readDailyAll(ctx, _uiState.value.days.toLong())
                _uiState.value = _uiState.value.copy(rows = result, loading = false)
            } catch (e: Exception) {
                val msg = e.message ?: e.toString()
                val pretty = if (
                    msg.contains("rate limited", ignoreCase = true) ||
                    msg.contains("quota", ignoreCase = true) ||
                    msg.contains("request rejected", ignoreCase = true)
                ) "요청이 너무 빠르게 전송되었습니다. 잠시 후 다시 시도해 주세요."
                else msg
                _uiState.value = _uiState.value.copy(error = pretty, loading = false)
            }
        }
    }
}
