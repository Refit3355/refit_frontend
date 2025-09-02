package com.refit.app.data.product.mapper

import com.refit.app.data.myfit.model.ProductRecommendationDto
import com.refit.app.data.product.model.Product

fun ProductRecommendationDto.toProduct(): Product {

    return Product(
        id = productId,
        image = thumbnailUrl,
        brand = brandName,
        name = productName,
        discountRate = discountRate,
        price = price,
        discountedPrice = discountedPrice
    )
}