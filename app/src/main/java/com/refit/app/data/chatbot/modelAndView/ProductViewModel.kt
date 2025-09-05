package com.refit.app.data.chatbot.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.chatbot.usecase.GetProductsUseCase
import com.refit.app.data.product.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import android.util.Log
private const val TAG = "ProductVM"

class ProductViewModel(
    private val getProducts: GetProductsUseCase
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val items: List<Product> = emptyList(),
        val totalCount: Int = 0,
        val error: String? = null,
        val lastId: Long? = null,   // 페이징용
        val hasMore: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun fetchFirst(templateId: String, effectCode: Int, sort: String = "latest") {
        Log.d(TAG, "fetchFirst() start: templateId=$templateId, effectCode=$effectCode, sort=$sort")
        _uiState.value = UiState(isLoading = true)
        viewModelScope.launch {
            val result = getProducts(
                GetProductsUseCase.Params(
                    currentTemplateId = templateId,
                    effectCode = effectCode,
                    lastId = null,
                    limit = 20,
                    sort = sort
                )
            )
            result.onSuccess { out ->
                val last = out.items.lastOrNull()?.id
                Log.d(TAG, "fetchFirst() success: items=${out.items.size}, total=${out.totalCount}, lastId=$last")
                _uiState.value = UiState(
                    isLoading = false,
                    items = out.items,
                    totalCount = out.totalCount,
                    lastId = last,
                    hasMore = out.items.isNotEmpty()
                )
                Log.d(TAG, "fetchFirst() state: hasMore=${_uiState.value.hasMore}")
            }.onFailure { e ->
                Log.e(TAG, "fetchFirst() failure: ${e.message}", e)
                _uiState.value = UiState(
                    isLoading = false,
                    error = e.message ?: "알 수 없는 오류가 발생했습니다."
                )
            }
        }
    }

    fun fetchMore(templateId: String, effectCode: Int, sort: String = "latest") {
        val current = _uiState.value
        Log.d(TAG, "fetchMore() gate: isLoading=${current.isLoading}, hasMore=${current.hasMore}, lastId=${current.lastId}, sort=$sort")
        if (current.isLoading || !current.hasMore) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true)
            Log.d(TAG, "fetchMore() start: templateId=$templateId, effectCode=$effectCode, lastId=${current.lastId}, sort=$sort")

            val result = getProducts(
                GetProductsUseCase.Params(
                    currentTemplateId = templateId,
                    effectCode = effectCode,
                    lastId = current.lastId,
                    limit = 20,
                    sort = sort
                )
            )
            result.onSuccess { out ->
                val merged = current.items + out.items
                val last = out.items.lastOrNull()?.id ?: current.lastId
                Log.d(TAG, "fetchMore() success: added=${out.items.size}, merged=${merged.size}, newLastId=$last")

                _uiState.value = current.copy(
                    isLoading = false,
                    items = merged,
                    totalCount = out.totalCount,
                    lastId = last,
                    hasMore = out.items.isNotEmpty()
                )
                Log.d(TAG, "fetchMore() state: hasMore=${_uiState.value.hasMore}")
            }.onFailure { e ->
                Log.e(TAG, "fetchMore() failure: ${e.message}", e)
                _uiState.value = current.copy(
                    isLoading = false,
                    error = e.message ?: "알 수 없는 오류가 발생했습니다."
                )
            }
        }
    }
}
