package com.refit.app.data.me.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.me.model.OrdersResponse
import com.refit.app.data.me.repository.MeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val orders: OrdersResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrderViewModel(
    private val repo: MeRepository = MeRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(OrderUiState())
    val state: StateFlow<OrderUiState> = _state.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repo.getOrders()
            _state.value = result.fold(
                onSuccess = { response ->
                    // 최신순 정렬
                    val sortedOrders = response.recentOrder.sortedByDescending { order ->
                        order.items.minOfOrNull { it.createdAt }
                    }
                    OrderUiState(
                        orders = response.copy(recentOrder = sortedOrders),
                        isLoading = false
                    )
                },
                onFailure = { OrderUiState(isLoading = false, error = it.message) }
            )
        }
    }
}

