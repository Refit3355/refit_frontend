package com.refit.app.data.combination.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.combination.model.CombinationDto
import com.refit.app.data.combination.repository.CombinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CombinationUiState(
    val combinations: List<CombinationDto> = emptyList(),
    val totalCount: Long = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null
)

class CombinationViewModel(
    private val repo: CombinationRepository = CombinationRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CombinationUiState())
    val state: StateFlow<CombinationUiState> = _state

    private var lastCombinationId: Long? = null

    /** 첫 페이지 로드 */
    fun loadCombinations(type: String, sort: String, keyword: String? = null, searchMode: String? = null, limit: Int = 10) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repo.getCombinations(type, sort, null, limit, keyword, searchMode)
            _state.value = result.fold(
                onSuccess = {
                    lastCombinationId = it.combinations.lastOrNull()?.combinationId
                    CombinationUiState(
                        combinations = it.combinations,
                        totalCount = it.totalCount,
                        isLoading = false,
                        hasMore = it.combinations.size >= limit
                    )
                },
                onFailure = {
                    CombinationUiState(error = it.message, isLoading = false)
                }
            )
        }
    }

    /** 추가 페이지 로드 */
    fun loadMoreCombinations(type: String, sort: String, limit: Int = 10) {
        if (_state.value.isLoadingMore || !_state.value.hasMore) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)

            val result = repo.getCombinations(type, sort, lastCombinationId, limit)
            _state.value = result.fold(
                onSuccess = {
                    val newList = _state.value.combinations + it.combinations
                    lastCombinationId = it.combinations.lastOrNull()?.combinationId
                    _state.value.copy(
                        combinations = newList,
                        totalCount = _state.value.totalCount,
                        isLoadingMore = false,
                        hasMore = it.combinations.isNotEmpty()
                    )
                },
                onFailure = {
                    _state.value.copy(isLoadingMore = false, error = it.message)
                }
            )
        }
    }

    /** 조합 저장 수 업데이트 */
    fun updateCombinations(newList: List<CombinationDto>) {
        _state.value = _state.value.copy(combinations = newList)
    }
}
