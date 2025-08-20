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
    val cartId: Long? = null,          // 단건 작업용
    val newQty: Int? = null,           // 수량 수정용
    val deletedIds: List<Long>? = null,// 여러건 삭제용
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
            _opEvents.tryEmit(
                CartOpResult(
                    type = CartOpType.UPDATE_QTY,
                    success = false,
                    cartId = cartId,
                    newQty = quantity,
                    message = "수량은 1 이상이어야 합니다."
                )
            )
            return
        }
        viewModelScope.launch {
            when (repo.updateQuantity(cartId, quantity)) {
                is CartResult.Ok -> {
                    _opEvents.emit(
                        CartOpResult(
                            type = CartOpType.UPDATE_QTY,
                            success = true,
                            cartId = cartId,
                            newQty = quantity
                        )
                    )
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.UPDATE_QTY,
                        success = false,
                        cartId = cartId,
                        newQty = quantity,
                        message = "로그인이 필요합니다."
                    )
                )
                is CartResult.Error -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.UPDATE_QTY,
                        success = false,
                        cartId = cartId,
                        newQty = quantity,
                        message = "수량 변경 실패"
                    )
                )
                else -> Unit
            }
        }
    }

    /** 삭제 (단건) */
    fun deleteOne(cartId: Long) {
        viewModelScope.launch {
            when (repo.deleteOne(cartId)) {
                is CartResult.Ok -> {
                    _opEvents.emit(
                        CartOpResult(
                            type = CartOpType.DELETE_ONE,
                            success = true,
                            cartId = cartId,
                            deletedIds = listOf(cartId)
                        )
                    )
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.DELETE_ONE,
                        success = false,
                        cartId = cartId,
                        message = "로그인이 필요합니다."
                    )
                )
                is CartResult.Error -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.DELETE_ONE,
                        success = false,
                        cartId = cartId,
                        message = "삭제 실패"
                    )
                )
                else -> Unit
            }
        }
    }

    /** 삭제 (여러건) */
    fun deleteBulk(ids: List<Long>) {
        if (ids.isEmpty()) {
            _opEvents.tryEmit(
                CartOpResult(
                    type = CartOpType.DELETE_BULK,
                    success = false,
                    deletedIds = emptyList(),
                    message = "선택된 항목이 없습니다."
                )
            )
            return
        }
        viewModelScope.launch {
            when (repo.deleteBulk(ids)) {
                is CartResult.Ok -> {
                    _opEvents.emit(
                        CartOpResult(
                            type = CartOpType.DELETE_BULK,
                            success = true,
                            deletedIds = ids
                        )
                    )
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.DELETE_BULK,
                        success = false,
                        deletedIds = ids,
                        message = "로그인이 필요합니다."
                    )
                )
                is CartResult.Error -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.DELETE_BULK,
                        success = false,
                        deletedIds = ids,
                        message = "일괄 삭제 실패"
                    )
                )
                else -> Unit
            }
        }
    }

    /** 추가 (단건) */
    fun addOne(productId: Long, quantity: Int) {
        if (quantity < 1) {
            _opEvents.tryEmit(
                CartOpResult(
                    type = CartOpType.ADD_ONE,
                    success = false,
                    message = "수량은 1 이상이어야 합니다."
                )
            )
            return
        }
        viewModelScope.launch {
            when (repo.addOne(productId, quantity)) {
                is CartResult.Ok -> {
                    _opEvents.emit(
                        CartOpResult(
                            type = CartOpType.ADD_ONE,
                            success = true
                        )
                    )
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.ADD_ONE,
                        success = false,
                        message = "로그인이 필요합니다."
                    )
                )
                is CartResult.Error -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.ADD_ONE,
                        success = false,
                        message = "추가 실패"
                    )
                )
                else -> Unit
            }
        }
    }

    /** 추가 (여러건) */
    fun addBulk(items: List<CartAddRequest>) {
        if (items.isEmpty()) {
            _opEvents.tryEmit(
                CartOpResult(
                    type = CartOpType.ADD_BULK,
                    success = false,
                    message = "추가할 항목이 없습니다."
                )
            )
            return
        }
        if (items.any { it.quantity < 1 }) {
            _opEvents.tryEmit(
                CartOpResult(
                    type = CartOpType.ADD_BULK,
                    success = false,
                    message = "수량은 1 이상이어야 합니다."
                )
            )
            return
        }
        viewModelScope.launch {
            when (repo.addBulk(items)) {
                is CartResult.Ok -> {
                    _opEvents.emit(
                        CartOpResult(
                            type = CartOpType.ADD_BULK,
                            success = true
                        )
                    )
                    badgeVm?.onCartChanged()
                }
                is CartResult.Unauth -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.ADD_BULK,
                        success = false,
                        message = "로그인이 필요합니다."
                    )
                )
                is CartResult.Error -> _opEvents.emit(
                    CartOpResult(
                        type = CartOpType.ADD_BULK,
                        success = false,
                        message = "일괄 추가 실패"
                    )
                )
                else -> Unit
            }
        }
    }
}
