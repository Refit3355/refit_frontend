package com.refit.app.data.cart.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.cart.model.CartItemDto
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.cart.repository.CartResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartListViewModel(
    private val repo: CartRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItemDto>>(emptyList())
    val items: StateFlow<List<CartItemDto>> = _items.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            when (val r = repo.fetchCart()) {
                is CartResult.ListSuccess -> _items.value = r.items
                is CartResult.Error       -> _error.value = r.msg
                is CartResult.Unauth      -> _error.value = "로그인이 필요합니다"
                else -> {}
            }
            _loading.value = false
        }
    }

    fun totalPrice(): Int =
        _items.value.sumOf { it.cartCnt * it.discountedPrice }

    fun applyLocalQuantity(cartId: Long, newQty: Int) {
        _items.update { list -> list.map { if (it.cartId == cartId) it.copy(cartCnt = newQty) else it } }
    }

    fun removeLocal(cartId: Long) {
        _items.update { list -> list.filterNot { it.cartId == cartId } }
    }
}
