package com.refit.app.data.product.model

data class ProductsPage(
    val items: List<Product>,
    val totalCount: Int,
    val hasMore: Boolean,
    val nextCursor: String?
)
