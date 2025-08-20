package com.refit.app.data.product.model

data class ProductListResponse(
    val items: List<ProductDto>,
    val totalCount: Int,
    val hasMore: Boolean,
    val nextCursor: String?
)

data class Product(
    val id: Long,
    val image: String,
    val brand: String,
    val name: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int
)

data class ProductDto(
    val id: Long,
    val thumbnailUrl: String,
    val brandName: String,
    val productName: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int,
    val sales: Int
)

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        image = this.thumbnailUrl,
        brand = this.brandName,
        name = this.productName,
        discountRate = this.discountRate,
        price = this.price,
        discountedPrice = this.discountedPrice
    )
}

data class ProductsPage(
    val items: List<Product>,
    val totalCount: Int,
    val hasMore: Boolean,
    val nextCursor: String?
)

data class ProductUiState(
    val items: List<Product> = emptyList(),
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)