package com.refit.app.data.chat.model

data class ProductSnippet(
    val id: Long,
    val thumbnailUrl: String?,
    val brandName: String?,
    val productName: String,
    val discountRate: Int,
    val price: Int,
    val discountedPrice: Int,
    val sales: Int
)

fun ProductSnippetDto.toDomain(): ProductSnippet = ProductSnippet(
    id = id,
    thumbnailUrl = thumbnailUrl,
    brandName = brandName,
    productName = productName,
    discountRate = discountRate ?: 0,
    price = price,
    discountedPrice = discountedPrice ?: price,
    sales = sales ?: 0
)