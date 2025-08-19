package com.refit.app.data.product.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.local.search.SearchHistoryStore
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.usecase.GetPopularProductsUseCase
import com.refit.app.data.product.usecase.GetSearchProductsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SearchMode { Suggest, Result }

data class SearchUiState(
    val mode: SearchMode = SearchMode.Suggest,
    val query: String = "",
    val items: List<Product> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val sort: String? = null,

    // Suggest 전용
    val recentQueries: List<String> = emptyList(),
    val popular: List<Product> = emptyList(),
    val popularLoading: Boolean = false
)

class SearchViewModel(
    private val getSearchProducts: GetSearchProductsUseCase = GetSearchProductsUseCase(),
    private val getPopularProducts: GetPopularProductsUseCase = GetPopularProductsUseCase(),
    private val history: SearchHistoryStore
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        // 앱 진입 시 자동으로 최근 검색어 수집
        viewModelScope.launch {
            history.historyFlow.collect { list ->
                _state.update { it.copy(recentQueries = list) }
            }
        }
        // 첫 진입에 인기상품 프리로드
        fetchPopularIfNeeded()
    }

    fun updateQuery(q: String) { _state.update { it.copy(query = q) } }


    fun enterSuggestMode() {
        _state.update {
            it.copy(
                mode = SearchMode.Suggest,
                items = emptyList(),
                totalCount = 0,
                hasMore = false,
                nextCursor = null,
                error = null
            )
        }
        fetchPopularIfNeeded()
    }

    private fun fetchPopularIfNeeded(limit: Int = 10) {
        if (_state.value.popular.isNotEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(popularLoading = true) }
            getPopularProducts(limit)
                .onSuccess { list ->
                    _state.update { it.copy(popular = list, popularLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(popularLoading = false, error = e.message) }
                }
        }
    }


    fun submitSearch() {
        val q = _state.value.query.trim()
        if (q.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, items = emptyList(), nextCursor = null) }
            getSearchProducts(q, cursor = null, limit = 20, sort = _state.value.sort)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            mode = SearchMode.Result,
                            items = page.items,
                            totalCount = page.totalCount,
                            hasMore = page.hasMore,
                            nextCursor = page.nextCursor,
                            isLoading = false
                        )
                    }
                    viewModelScope.launch { history.add(q) }
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

    fun submitFromHistory(q: String) {
        _state.update { it.copy(query = q) }
        submitSearch()
    }

    fun clearQuery() {
        _state.update { it.copy(query = "") }
        enterSuggestMode()
    }

    fun removeHistory(q: String) = viewModelScope.launch { history.remove(q) }

}
