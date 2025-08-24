package com.refit.app.data.me.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.me.model.OrdersResponse
import com.refit.app.data.me.model.UpdateOrderStatusResponse
import com.refit.app.data.me.repository.MeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val orders: OrdersResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionMessage: String? = null
)

class OrderViewModel(
    private val repo: MeRepository = MeRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(OrderUiState())
    val state: StateFlow<OrderUiState> = _state.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, actionMessage = null)
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

    // 교환 신청
    fun requestExchange(orderItemId: Long) {
        viewModelScope.launch {
            val result = repo.requestExchange(orderItemId)
            _state.value = result.fold(
                onSuccess = { res: UpdateOrderStatusResponse ->
                    val currentOrders = _state.value.orders
                    val updatedOrders = currentOrders?.copy(
                        recentOrder = currentOrders.recentOrder.map { order ->
                            order.copy(
                                items = order.items.map { item ->
                                    if (item.orderItemId == orderItemId) {
                                        item.copy(status = 4) // 교환 신청중
                                    } else item
                                }
                            )
                        }
                    )
                    _state.value.copy(
                        orders = updatedOrders,
                        actionMessage = res.message
                    )
                },
                onFailure = {
                    _state.value.copy(actionMessage = "교환 신청 실패: ${it.message}")
                }
            )
        }
    }

    // 반품 신청
    fun requestReturn(orderItemId: Long) {
        viewModelScope.launch {
            val result = repo.requestReturn(orderItemId)
            _state.value = result.fold(
                onSuccess = { res: UpdateOrderStatusResponse ->
                    val currentOrders = _state.value.orders
                    val updatedOrders = currentOrders?.copy(
                        recentOrder = currentOrders.recentOrder.map { order ->
                            order.copy(
                                items = order.items.map { item ->
                                    if (item.orderItemId == orderItemId) {
                                        item.copy(status = 6) // 반품 신청중
                                    } else item
                                }
                            )
                        }
                    )
                    _state.value.copy(
                        orders = updatedOrders,
                        actionMessage = res.message
                    )
                },
                onFailure = {
                    _state.value.copy(actionMessage = "반품 신청 실패: ${it.message}")
                }
            )
        }
    }
}
