package com.refit.app.data.combination.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.combination.model.CombinationDetailResponse
import com.refit.app.data.combination.repository.CombinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CombinationDetailUiState(
    val detail: CombinationDetailResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CombinationDetailViewModel(
    private val repo: CombinationRepository = CombinationRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(CombinationDetailUiState())
    val state: StateFlow<CombinationDetailUiState> = _state

    fun loadCombinationDetail(id: Long) {
        viewModelScope.launch {
            _state.value = CombinationDetailUiState(isLoading = true)
            val result = repo.getCombinationDetail(id)
            _state.value = result.fold(
                onSuccess = { CombinationDetailUiState(detail = it) },
                onFailure = { CombinationDetailUiState(error = it.message) }
            )
        }
    }
}
