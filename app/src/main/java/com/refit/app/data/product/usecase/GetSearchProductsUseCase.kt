package com.refit.app.data.product.usecase

import com.refit.app.data.product.model.Product
import com.refit.app.data.product.model.ProductsPage
import com.refit.app.data.product.repository.ProductRepository

class GetSearchProductsUseCase(
    private val repo: ProductRepository = ProductRepository()
) {
    suspend operator fun invoke(
        query: String,
        cursor: String?,
        limit: Int,
        sort: String?
    ): Result<ProductsPage> = repo.searchProducts(query, cursor, limit, sort)
        .map { res ->
            ProductsPage(
                items = res.items.map { dto ->
                    Product(
                        id = dto.id,
                        image = dto.thumbnailUrl,
                        brand = dto.brandName,
                        name  = dto.productName,
                        discountRate = dto.discountRate,
                        price = dto.price,
                        discountedPrice = dto.discountedPrice
                    )
                },
                totalCount = res.totalCount,
                hasMore    = res.hasMore,
                nextCursor = res.nextCursor
            )
        }
}
