package com.refit.app.data.chat.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductSnippetDto(
    val id: Long,
    val thumbnailUrl: String? = null,
    val brandName: String? = null,
    val productName: String,
    val discountRate: Int? = null,
    val price: Int,
    val discountedPrice: Int? = null,
    val sales: Int? = null
)
