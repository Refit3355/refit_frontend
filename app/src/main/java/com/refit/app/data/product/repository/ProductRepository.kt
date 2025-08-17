package com.refit.app.data.product.repository

import com.refit.app.network.RetrofitInstance
import com.refit.app.data.product.api.ProductApi
import com.refit.app.data.product.model.ProductListResponse

class ProductRepository(
    private val api: ProductApi = RetrofitInstance.create(ProductApi::class.java)
) {
    suspend fun fetchProducts(
        sort: String, limit: Int, group: String, cursor: String?, category: Int?
    ): Result<ProductListResponse> = runCatching {
        api.getProducts(sort = sort, limit = limit, group = group, cursor = cursor, category = category)
    }
}