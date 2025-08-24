package com.refit.app.data.me.modelAndView


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.local.combination.model.CombinationDto
import com.refit.app.data.me.repository.MeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MyCombinationUiState(
    val combinations: List<CombinationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MyCombinationViewModel(
    private val repo: MeRepository = MeRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(MyCombinationUiState())
    val state: StateFlow<MyCombinationUiState> = _state

    fun loadMyCombinations() {
        viewModelScope.launch {
            _state.value = MyCombinationUiState(isLoading = true)
            val result = repo.getCreatedCombinations()
            _state.value = result.fold(
                onSuccess = { MyCombinationUiState(combinations = it.combinations) },
                onFailure = { MyCombinationUiState(error = it.message) }
            )
        }
    }
}
