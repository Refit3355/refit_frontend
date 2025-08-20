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

    /** 앱 진입/포그라운드/장바구니 변경 시 호출 */
    fun refreshCount() {
        viewModelScope.launch {
            when (val r = repo.fetchCount()) {
                is CartResult.CountSuccess -> _badgeCount.value = r.count
                is CartResult.Unauth       -> _badgeCount.value = 0
                else                       -> Unit // Error/Ok/ListSuccess 등은 배지 갱신 대상 아님
            }
        }
    }

    /** 다른 화면(장바구니 목록 등)에서 추가/삭제/수정 성공 후 호출 */
    fun onCartChanged() {
        refreshCount()
    }

    /** 초기 표시용으로 바로 값 주입 */
    fun setCountDirect(count: Int) { _badgeCount.value = count }
}
