package com.refit.app.data.product.usecase

import com.refit.app.data.product.model.Product
import com.refit.app.data.product.repository.ProductRepository

class GetPopularProductsUseCase(
    private val repo: ProductRepository = ProductRepository()
) {
    suspend operator fun invoke(limit: Int): Result<List<Product>> =
        repo.fetchPopularProducts(limit).map { list ->
            list.map { dto ->
                Product(
                    id = dto.id,
                    image = dto.thumbnailUrl,
                    brand = dto.brandName,
                    name  = dto.productName,
                    discountRate = dto.discountRate,
                    price = dto.price,
                    discountedPrice = dto.discountedPrice
                )
            }
        }
}
