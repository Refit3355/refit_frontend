package com.refit.app.data.product.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.repository.RecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecommendationUiState(
    val items: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecommendationViewModel(
    private val repo: RecommendationRepository = RecommendationRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RecommendationUiState())
    val state: StateFlow<RecommendationUiState> = _state

    fun loadRecommendations(type: Int, limit: Int = 10) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repo.fetchRecommendations(type, limit)
                .onSuccess { products ->
                    _state.value = _state.value.copy(items = products, isLoading = false)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
        }
    }
}
