package com.refit.app.data.product.repository

import com.refit.app.network.RetrofitInstance
import com.refit.app.data.product.api.ProductApi
import com.refit.app.data.product.model.ProductDetail
import com.refit.app.data.product.model.ProductDto
import com.refit.app.data.product.model.ProductImage
import com.refit.app.data.product.model.ProductListResponse

class ProductRepository(
    private val api: ProductApi = RetrofitInstance.create(ProductApi::class.java)
) {
    suspend fun fetchProducts(
        sort: String, limit: Int, group: String, cursor: String?, category: Int?
    ): Result<ProductListResponse> = runCatching {
        api.getProducts(sort = sort, limit = limit, group = group, cursor = cursor, category = category)
    }

    suspend fun fetchProductDetail(id: Int): Result<ProductDetail> = runCatching {
        val res = api.getProductDetail(id)
        ProductDetail(
            id = res.product.id,
            brand = res.product.brandName,
            name = res.product.productName,
            thumbnailUrl = res.product.thumbnailUrl,
            discountRate = res.product.discountRate,
            price = res.product.price,
            discountedPrice = res.product.discountedPrice,
            recommendedPeriod = res.product.recommendedPeriod,
            images = res.images.map { ProductImage(it.id, it.url, it.order) }
        )
    }

    suspend fun searchProducts(
        query: String,
        cursor: String?,
        limit: Int,
        sort: String?
    ): Result<ProductListResponse> = runCatching {
        api.searchProducts(q = query, cursor = cursor, limit = limit, sort = sort)
    }

    suspend fun fetchPopularProducts(limit: Int): Result<List<ProductDto>> = runCatching {
        api.getPopularProducts(limit)
    }
}