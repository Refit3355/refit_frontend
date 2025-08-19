package com.refit.app.data.product.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.usecase.GetSearchProductsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val items: List<Product> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val sort: String? = null
)

class SearchViewModel(
    private val getSearchProducts: GetSearchProductsUseCase = GetSearchProductsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    fun updateQuery(q: String) { _state.update { it.copy(query = q) } }

    fun submitSearch() {
        val q = _state.value.query.trim()
        if (q.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, items = emptyList(), nextCursor = null) }
            getSearchProducts(q, cursor = null, limit = 20, sort = _state.value.sort)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            items = page.items,
                            totalCount = page.totalCount,
                            hasMore = page.hasMore,
                            nextCursor = page.nextCursor,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "검색 실패") }
                }
        }
    }

    fun loadMore() {
        val s = _state.value
        if (!s.hasMore || s.isLoading || s.query.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getSearchProducts(s.query, cursor = s.nextCursor, limit = 20, sort = s.sort)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            items = it.items + page.items,
                            totalCount = page.totalCount,
                            hasMore = page.hasMore,
                            nextCursor = page.nextCursor,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "더 불러오기 실패") }
                }
        }
    }

    fun changeSort(newSort: String?) {
        _state.update { it.copy(sort = newSort) }
        submitSearch()
    }
}
