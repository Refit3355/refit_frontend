package com.refit.app.data.product.model

data class ProductUiState(
    val items: List<Product> = emptyList(),
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)