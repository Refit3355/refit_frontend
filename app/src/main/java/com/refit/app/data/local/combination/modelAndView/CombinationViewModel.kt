package com.refit.app.data.local.combination.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.local.combination.model.CombinationDto
import com.refit.app.data.local.combination.repository.CombinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CombinationUiState(
    val combinations: List<CombinationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isProcessing: Boolean = false  // 요청 중복 방지 flag
)

class LikedCombinationViewModel(
    private val repo: CombinationRepository = CombinationRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CombinationUiState())
    val state: StateFlow<CombinationUiState> = _state

    /** 찜한 조합 불러오기 */
    fun loadLikedCombinations(ids: Set<Long>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, message = null)
            val result = repo.getLikedCombinations(ids.toList())
            _state.value = result.fold(
                onSuccess = { CombinationUiState(combinations = it.combinations) },
                onFailure = { CombinationUiState(error = it.message) }
            )
        }
    }

    /** 좋아요 등록 */
    fun likeCombination(combinationId: Long) {
        if (_state.value.isProcessing) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessing = true)
            val result = repo.likeCombination(combinationId)
            _state.value = result.fold(
                onSuccess = { res ->
                    val updatedList = _state.value.combinations.map {
                        if (it.combinationId == combinationId) it.copy(likes = (it.likes ?: 0) + 1)
                        else it
                    }
                    _state.value.copy(combinations = updatedList, message = res.message, isProcessing = false)
                },
                onFailure = {
                    _state.value.copy(error = it.message, isProcessing = false)
                }
            )
        }
    }

    /** 좋아요 해제 */
    fun dislikeCombination(combinationId: Long) {
        if (_state.value.isProcessing) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessing = true)
            val result = repo.dislikeCombination(combinationId)
            _state.value = result.fold(
                onSuccess = { res ->
                    val updatedList = _state.value.combinations.map {
                        if (it.combinationId == combinationId) it.copy(likes = maxOf((it.likes ?: 0) - 1, 0))
                        else it
                    }
                    _state.value.copy(combinations = updatedList, message = res.message, isProcessing = false)
                },
                onFailure = {
                    _state.value.copy(error = it.message, isProcessing = false)
                }
            )
        }
    }
}
