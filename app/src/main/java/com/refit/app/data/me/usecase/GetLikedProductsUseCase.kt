package com.refit.app.data.me.usecase

import com.refit.app.data.me.repository.MeRepository
import com.refit.app.data.product.model.Product

class GetLikedProductsUseCase(
    private val repo: MeRepository = MeRepository()
) {
    suspend operator fun invoke(ids: Set<Long>): Result<List<Product>> =
        repo.getLikedProducts(ids)
}