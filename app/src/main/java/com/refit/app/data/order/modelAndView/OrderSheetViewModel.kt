package com.refit.app.data.order.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.order.api.OrderApi
import com.refit.app.data.order.model.*
import com.refit.app.data.payment.api.PaymentApi
import com.refit.app.data.payment.model.ConfirmPaymentRequest
import com.refit.app.data.payment.model.ConfirmPaymentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OrderSheetUi(
    val loading: Boolean = true,
    val error: String? = null,
    val draft: DraftOrderResponse? = null,
    val consentTerms: Boolean = false,
    val selectedMethod: String = "CARD" // "CARD","TRANSFER","VIRTUAL_ACCOUNT","MOBILE_PHONE",...
)

class OrderSheetViewModel(
    private val orderApi: OrderApi,
    private val paymentApi: PaymentApi
) : ViewModel() {

    private val _ui = MutableStateFlow(OrderSheetUi())
    val ui: StateFlow<OrderSheetUi> = _ui

    fun loadDraft(req: DraftOrderRequest) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            runCatching {
                orderApi.createDraft(req)
            }.onSuccess { draft ->
                _ui.value = _ui.value.copy(loading = false, draft = draft)
            }.onFailure {
                _ui.value = _ui.value.copy(loading = false, error = it.message ?: "주문서 생성 실패")
            }
        }
    }

    fun toggleTerms(checked: Boolean) {
        _ui.value = _ui.value.copy(consentTerms = checked)
    }

    fun selectMethod(method: String) {
        _ui.value = _ui.value.copy(selectedMethod = method)
    }

    suspend fun confirm(paymentKey: String, orderId: String, amount: Long): ConfirmPaymentResponse {
        return paymentApi.confirm(ConfirmPaymentRequest(paymentKey, orderId, amount))
    }
}
