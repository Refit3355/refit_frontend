package com.refit.app.data.product.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.product.model.ProductDetail
import com.refit.app.data.product.usecase.GetProductDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: ProductDetail? = null
)

class ProductDetailViewModel(
    private val getDetail: GetProductDetailUseCase = GetProductDetailUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state: StateFlow<ProductDetailUiState> = _state

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = ProductDetailUiState(isLoading = true)
            getDetail(id)
                .onSuccess { _state.value = ProductDetailUiState(data = it) }
                .onFailure { _state.value = ProductDetailUiState(error = it.message) }
        }
    }
}
