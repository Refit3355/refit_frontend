package com.refit.app.data.me.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.me.model.OrdersResponse
import com.refit.app.data.me.model.UpdateOrderStatusResponse
import com.refit.app.data.me.repository.MeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val orders: OrdersResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionMessage: String? = null
)

sealed class OrderUiEvent {
    data object PartialCancelFailed : OrderUiEvent()
    data class CancelRequestSucceeded(val message: String) : OrderUiEvent()
}

class OrderViewModel(
    private val repo: MeRepository = MeRepository()
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<OrderUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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

    // 주문 취소
    fun requestCancel(orderItemId: Long, unitPrice: Int, count: Int, reason: String) {
        viewModelScope.launch {
            try {
                val result = repo.requestCancel(orderItemId, unitPrice, count, reason)
                _state.value = result.fold(
                    onSuccess = { res: UpdateOrderStatusResponse ->
                        val current = _state.value.orders
                        val updated = current?.copy(
                            recentOrder = current.recentOrder.map { order ->
                                order.copy(
                                    items = order.items.map { item ->
                                        if (item.orderItemId == orderItemId) {
                                            item.copy(status = 3) // 3 = 취소
                                        } else item
                                    }
                                )
                            }
                        )
                        // 성공 이벤트 발행 → 화면에서 성공 모달 표시
                        _uiEvent.emit(
                            OrderUiEvent.CancelRequestSucceeded(
                                (res.message ?: "").ifBlank { "결제 취소 신청되었습니다." }
                            )
                        )
                        // 상태 갱신(메시지는 이벤트로 처리하므로 actionMessage는 비워도 OK)
                        _state.value.copy(orders = updated, actionMessage = null)
                    },
                    onFailure = {
                        // 실패 이벤트 → 실패 모달
                        _uiEvent.emit(OrderUiEvent.PartialCancelFailed)
                        _state.value // 상태 유지
                    }
                )
            } catch (_: Throwable) {
                _uiEvent.emit(OrderUiEvent.PartialCancelFailed)
            }
        }
    }
}