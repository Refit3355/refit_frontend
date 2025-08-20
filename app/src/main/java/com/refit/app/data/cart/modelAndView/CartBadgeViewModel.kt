package com.refit.app.data.cart.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.cart.repository.CartResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartBadgeViewModel(
    private val repo: CartRepository
) : ViewModel() {

    private val _badgeCount = MutableStateFlow(0)
    val badgeCount: StateFlow<Int> = _badgeCount.asStateFlow()

    fun refreshCount() {
        viewModelScope.launch {
            when (val r = repo.fetchCount()) {
                is CartResult.Success -> _badgeCount.value = r.count
                is CartResult.Unauth  -> _badgeCount.value = 0   // 토큰 만료 등
                is CartResult.Error   -> { /* 실패 시 유지 or 0 */ }
            }
        }
    }

    fun setCountDirect(count: Int) { _badgeCount.value = count }
}
