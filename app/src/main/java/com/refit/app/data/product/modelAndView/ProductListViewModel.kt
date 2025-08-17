package com.refit.app.data.product.modelAndView

import GetProductsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.product.model.ProductUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val getProducts: GetProductsUseCase = GetProductsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(ProductUiState())
    val state: StateFlow<ProductUiState> = _state

    private var sort = "sales"
    private var group = "beauty"
    private var limit = 20
    private var category: Int? = null

    fun setGroup(new: String) { if (group != new) { group = new; loadFirstPage(sort, group, limit, category) } }
    fun setSort(new: String)  { if (sort  != new) { sort  = new; loadFirstPage(sort, group, limit, category) } }
    fun setCategory(new: Int?) { if (category != new) { category = new; loadFirstPage(sort, group, limit, category) } }

    fun loadFirstPage(
        sort: String = this.sort,
        group: String = this.group,
        limit: Int = this.limit,
        category: Int? = this.category
    ) {
        this.sort = sort; this.group = group; this.limit = limit; this.category = category
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getProducts(sort, limit, group, cursor = null, category = category)
                .onSuccess { page ->
                    _state.value = _state.value.copy(
                        items = page.items,
                        totalCount = page.totalCount,
                        hasMore = page.hasMore,
                        nextCursor = page.nextCursor,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun loadNextPage() {
        val cursor = _state.value.nextCursor ?: return
        if (!_state.value.hasMore || _state.value.isLoading) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getProducts(sort, limit, group, cursor = cursor, category = category)
                .onSuccess { page ->
                    _state.value = _state.value.copy(
                        items = _state.value.items + page.items,
                        totalCount = page.totalCount,
                        hasMore = page.hasMore,
                        nextCursor = page.nextCursor,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
        }
    }

}
