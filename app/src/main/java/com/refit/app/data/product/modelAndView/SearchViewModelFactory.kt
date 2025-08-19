package com.refit.app.data.product.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.refit.app.data.local.search.SearchHistoryStore
import com.refit.app.data.product.usecase.GetPopularProductsUseCase
import com.refit.app.data.product.usecase.GetSearchProductsUseCase

class SearchViewModelFactory(
    private val history: SearchHistoryStore,
    private val getSearchProducts: GetSearchProductsUseCase = GetSearchProductsUseCase(),
    private val getPopularProducts: GetPopularProductsUseCase = GetPopularProductsUseCase()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SearchViewModel::class.java))
        return SearchViewModel(
            getSearchProducts = getSearchProducts,
            getPopularProducts = getPopularProducts,
            history = history
        ) as T
    }
}
