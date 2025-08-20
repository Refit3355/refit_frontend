package com.refit.app.data.cart.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.cart.model.CartAddRequest
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.cart.repository.CartResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/** 어떤 작업이 끝났는지 구분용 */
enum class CartOpType {
    ADD_ONE, ADD_BULK, DELETE_ONE, DELETE_BULK, UPDATE_QTY
}

/** 작업 결과 이벤트 */
data class CartOpResult(
    val type: CartOpType,
    val success: Boolean,
    val message: String? = null
)

class CartEditViewModel(
    private val repo: CartRepository,
    private val badgeVm: CartBadgeViewModel?
) : ViewModel() {

    private val _opEvents = MutableSharedFlow<CartOpResult>(extraBufferCapacity = 1)
    val opEvents: SharedFlow<CartOpResult> = _opEvents

    /** 수량 수정 (단건) */
    fun updateQuantity(cartId: Long, quantity: Int) {
        if (quantity < 1) {
            _opEvents.tryEmit(CartOpResult(CartOpType.UPDATE_QTY, false, "수량은 1 이상이어야 합니다."))
            return
        }
        viewModelScope.launch {
            when (repo.updateQuantity(cartId, quantity)) {
                is CartResult.Ok -> {
                    _opEvents.emit(CartOpResult(CartOpType.UPDATE_QTY, true))
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(CartOpResult(CartOpType.UPDATE_QTY, false, "로그인이 필요합니다."))
                is CartResult.Error -> _opEvents.emit(CartOpResult(CartOpType.UPDATE_QTY, false, "수량 변경 실패"))
                else -> Unit
            }
        }
    }

    /** 삭제 (단건) */
    fun deleteOne(cartId: Long) {
        viewModelScope.launch {
            when (repo.deleteOne(cartId)) {
                is CartResult.Ok -> {
                    _opEvents.emit(CartOpResult(CartOpType.DELETE_ONE, true))
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(CartOpResult(CartOpType.DELETE_ONE, false, "로그인이 필요합니다."))
                is CartResult.Error -> _opEvents.emit(CartOpResult(CartOpType.DELETE_ONE, false, "삭제 실패"))
                else -> Unit
            }
        }
    }

    /** 삭제 (여러건) */
    fun deleteBulk(ids: List<Long>) {
        if (ids.isEmpty()) {
            _opEvents.tryEmit(CartOpResult(CartOpType.DELETE_BULK, false, "선택된 항목이 없습니다."))
            return
        }
        viewModelScope.launch {
            when (repo.deleteBulk(ids)) {
                is CartResult.Ok -> {
                    _opEvents.emit(CartOpResult(CartOpType.DELETE_BULK, true))
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(CartOpResult(CartOpType.DELETE_BULK, false, "로그인이 필요합니다."))
                is CartResult.Error -> _opEvents.emit(CartOpResult(CartOpType.DELETE_BULK, false, "일괄 삭제 실패"))
                else -> Unit
            }
        }
    }

    /** 추가 (단건) */
    fun addOne(productId: Long, quantity: Int) {
        if (quantity < 1) {
            _opEvents.tryEmit(CartOpResult(CartOpType.ADD_ONE, false, "수량은 1 이상이어야 합니다."))
            return
        }
        viewModelScope.launch {
            when (repo.addOne(productId, quantity)) {
                is CartResult.Ok -> {
                    _opEvents.emit(CartOpResult(CartOpType.ADD_ONE, true))
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(CartOpResult(CartOpType.ADD_ONE, false, "로그인이 필요합니다."))
                is CartResult.Error -> _opEvents.emit(CartOpResult(CartOpType.ADD_ONE, false, "추가 실패"))
                else -> Unit
            }
        }
    }

    /** 추가 (여러건) */
    fun addBulk(items: List<CartAddRequest>) {
        if (items.isEmpty()) {
            _opEvents.tryEmit(CartOpResult(CartOpType.ADD_BULK, false, "추가할 항목이 없습니다."))
            return
        }
        // 클라이언트 검증(0개 방지)
        if (items.any { it.quantity < 1 }) {
            _opEvents.tryEmit(CartOpResult(CartOpType.ADD_BULK, false, "수량은 1 이상이어야 합니다."))
            return
        }
        viewModelScope.launch {
            when (repo.addBulk(items)) {
                is CartResult.Ok -> {
                    _opEvents.emit(CartOpResult(CartOpType.ADD_BULK, true))
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(CartOpResult(CartOpType.ADD_BULK, false, "로그인이 필요합니다."))
                is CartResult.Error -> _opEvents.emit(CartOpResult(CartOpType.ADD_BULK, false, "일괄 추가 실패"))
                else -> Unit
            }
        }
    }
}
