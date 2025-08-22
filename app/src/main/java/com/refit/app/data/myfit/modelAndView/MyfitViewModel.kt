package com.refit.app.data.myfit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.myfit.model.MemberProductItem
import com.refit.app.data.myfit.repository.MyfitRepository
import com.refit.app.network.TokenManager
import com.refit.app.network.TokenManager.parseNicknameFromJwt
import kotlinx.coroutines.launch
import android.util.Log
import com.refit.app.data.myfit.model.PurchasedProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class MyfitType(val param: String) { BEAUTY("beauty"), HEALTH("health") }
enum class MyfitTab(val param: String) { USING("using"), COMPLETED("completed"), REGISTER("all") }
enum class ErrorKind { Server, Client, Network, Timeout, Unknown }
data class ErrorUi(val kind: ErrorKind, val code: Int? = null)

data class MyfitUiState(
    val type: MyfitType = MyfitType.BEAUTY,
    val tab: MyfitTab = MyfitTab.USING,
    val items: List<MemberProductItem> = emptyList(),     // using/completed
    val purchased: List<PurchasedProductDto> = emptyList(), // register 탭 전용
    val isLoading: Boolean = false,
    val error: ErrorUi? = null,
    val confirmCompleteId: Long? = null,
    val confirmDeleteId: Long? = null,
    val confirmUsingOrderItemId: Long? = null,
    val nickname: String = "회원",
)

private const val TAG = "MyfitVM"

class MyfitViewModel(
    private val repo: MyfitRepository = MyfitRepository()
) : ViewModel() {

    var ui by mutableStateOf(MyfitUiState())
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        setNicknameFromToken()
        refresh()
    }

    private fun setNicknameFromToken() {
        val token = TokenManager.getToken()
        val nickname = token?.let { parseNicknameFromJwt(it) } ?: "회원"
        ui = ui.copy(nickname = nickname)
    }

    private fun Throwable.toErrorUi(): ErrorUi = when (this) {
        is HttpException -> {
            val kind = if (code() in 500..599) ErrorKind.Server else ErrorKind.Client
            ErrorUi(kind = kind, code = code())
        }
        is UnknownHostException -> ErrorUi(ErrorKind.Network)
        is SocketTimeoutException -> ErrorUi(ErrorKind.Timeout)
        else -> ErrorUi(ErrorKind.Unknown)
    }

    init { refresh() }

    fun onTypeChange(type: MyfitType) { ui = ui.copy(type = type); refresh() }
    fun onTabChange(tab: MyfitTab) { ui = ui.copy(tab = tab); refresh() }

    fun refresh() = viewModelScope.launch {
        ui = ui.copy(isLoading = true, error = null)
        try {
            when (ui.tab) {
                MyfitTab.REGISTER -> {
                    val list = repo.getPurchasedProducts(ui.type.param).data
                    ui = ui.copy(isLoading = false, purchased = list, items = emptyList())
                }
                else -> {
                    val res = repo.getMemberProducts(ui.type.param, ui.tab.param)
                    ui = ui.copy(isLoading = false, items = res.items, purchased = emptyList())
                }
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val code = e.code()
                    val raw = e.response()?.errorBody()?.string()
                    val serverMsg = try { JSONObject(raw ?: "").optString("message", null) } catch (_: Exception) { null }
                    Log.e(TAG, "HTTP $code failed body=$raw msg=$serverMsg", e)
                    val kind = if (code in 500..599) ErrorKind.Server else ErrorKind.Client
                    ui = ui.copy(isLoading = false, error = ErrorUi(kind, code))
                }
                is UnknownHostException -> {
                    Log.e(TAG, "Network unreachable", e)
                    ui = ui.copy(isLoading = false, error = ErrorUi(ErrorKind.Network))
                }
                is SocketTimeoutException -> {
                    Log.e(TAG, "Timeout", e)
                    ui = ui.copy(isLoading = false, error = ErrorUi(ErrorKind.Timeout))
                }
                else -> {
                    Log.e(TAG, "Unknown error", e)
                    ui = ui.copy(isLoading = false, error = ErrorUi(ErrorKind.Unknown))
                }
            }
        }
    }

    fun askComplete(id: Long?) { ui = ui.copy(confirmCompleteId = id) }
    fun askDelete(id: Long?) { ui = ui.copy(confirmDeleteId = id) }
    fun askUsing(id: Long?) { ui = ui.copy(confirmUsingOrderItemId = id) }

    fun confirmUsing() = viewModelScope.launch {
        val id = ui.confirmUsingOrderItemId ?: return@launch
        ui = ui.copy(confirmUsingOrderItemId = null, isLoading = true)
        runCatching { repo.createFromOrderItem(id) }
            .onSuccess { refresh() }
            .onFailure { e -> ui = ui.copy(isLoading = false, error = e.toErrorUi()) }
    }


    fun confirmComplete() = viewModelScope.launch {
        val id = ui.confirmCompleteId ?: return@launch
        ui = ui.copy(confirmCompleteId = null, isLoading = true)
        runCatching { repo.completeMemberProduct(id) }
            .onSuccess { refresh() }
            .onFailure { e ->
                ui = ui.copy(isLoading = false, error = e.toErrorUi())
            }
    }

    fun confirmDelete() = viewModelScope.launch {
        val id = ui.confirmDeleteId ?: return@launch
        ui = ui.copy(confirmDeleteId = null, isLoading = true)
        runCatching { repo.deleteMemberProduct(id) }
            .onSuccess { refresh() }
            .onFailure { e ->
                ui = ui.copy(isLoading = false, error = e.toErrorUi())
            }
    }

    fun startUsing(orderItemId: Long) = viewModelScope.launch {
        _isLoading.value = true; _error.value = null
        runCatching { repo.createFromOrderItem(orderItemId) }
            .onSuccess {
                refresh()
            }
            .onFailure { e ->
                _error.value = e.message ?: "사용 등록 실패"
            }
        _isLoading.value = false
    }
}
