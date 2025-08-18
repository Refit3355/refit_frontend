import com.refit.app.data.product.model.Product
import com.refit.app.data.product.model.ProductsPage
import com.refit.app.data.product.repository.ProductRepository

class GetProductsUseCase(
    private val repo: ProductRepository = ProductRepository()
) {
    suspend operator fun invoke(
        sort: String,
        limit: Int,
        group: String,
        cursor: String?,
        category: Int?
    ): Result<ProductsPage> = repo.fetchProducts(sort, limit, group, cursor, category)
        .map { res ->
            ProductsPage(
                items = res.items.map { dto ->
                    Product(
                        id = dto.id,
                        image = dto.thumbnailUrl,
                        brand = dto.brandName,
                        name = dto.productName,
                        discountRate = dto.discountRate,
                        price = dto.price,
                        discountedPrice = dto.discountedPrice
                    )
                },
                totalCount = res.totalCount,
                hasMore = res.hasMore,
                nextCursor = res.nextCursor
            )
        }
}
