package com.refit.app.data.me.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.me.usecase.GetLikedProductsUseCase
import com.refit.app.data.product.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WishUiState(
    val items: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LikedProductsViewModel(
    private val getLikedProducts: GetLikedProductsUseCase = GetLikedProductsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(WishUiState())
    val state: StateFlow<WishUiState> = _state.asStateFlow()

    fun refresh(ids: Set<Long>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = getLikedProducts(ids)
            _state.value = result.fold(
                onSuccess = { WishUiState(items = it, isLoading = false, error = null) },
                onFailure = { _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}