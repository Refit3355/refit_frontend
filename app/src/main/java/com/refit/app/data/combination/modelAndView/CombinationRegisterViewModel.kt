package com.refit.app.data.combination.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.combination.model.CreateCombinationRequest
import com.refit.app.data.combination.repository.CombinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CombinationRegisterUiState(
    val isLoading: Boolean = false,
    val successId: String? = null,
    val error: String? = null
)

class CombinationRegisterViewModel(
    private val repo: CombinationRepository = CombinationRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CombinationRegisterUiState())
    val state: StateFlow<CombinationRegisterUiState> = _state

    fun registerCombination(req: CreateCombinationRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.createCombination(req)
                .onSuccess { res ->
                    _state.update { it.copy(isLoading = false, successId = res.combinationId) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
