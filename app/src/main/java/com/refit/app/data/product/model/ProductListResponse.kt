package com.refit.app.data.product.model

data class ProductListResponse(
    val items: List<ProductDto>,
    val totalCount: Int,
    val hasMore: Boolean,
    val nextCursor: String?
)