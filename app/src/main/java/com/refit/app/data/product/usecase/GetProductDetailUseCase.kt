package com.refit.app.data.product.usecase

import com.refit.app.data.product.model.ProductDetail
import com.refit.app.data.product.repository.ProductRepository

class GetProductDetailUseCase(
    private val repo: ProductRepository = ProductRepository()
) {
    suspend operator fun invoke(id: Int): Result<ProductDetail> =
        repo.fetchProductDetail(id)
}
