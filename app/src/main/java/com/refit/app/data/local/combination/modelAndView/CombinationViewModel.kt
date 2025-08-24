package com.refit.app.data.local.combination.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.local.combination.repository.CombinationRepository
import com.refit.app.data.local.combination.model.CombinationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CombinationUiState(
    val combinations: List<CombinationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LikedCombinationViewModel(
    private val repo: CombinationRepository = CombinationRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CombinationUiState())
    val state: StateFlow<CombinationUiState> = _state

    fun loadLikedCombinations(ids: Set<Long>) {
        viewModelScope.launch {
            _state.value = CombinationUiState(isLoading = true)
            val result = repo.getLikedCombinations(ids.toList())
            _state.value = result.fold(
                onSuccess = { CombinationUiState(combinations = it.combinations) },
                onFailure = { CombinationUiState(error = it.message) }
            )
        }
    }
}
