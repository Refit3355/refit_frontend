package com.refit.app.data.combination.model

data class CreateCombinationRequest(
    val name: String,
    val content: String,
    val type: String,
    val product1Id: Long,
    val product2Id: Long,
    val product3Id: Long? = null,
    val product4Id: Long? = null,
    val product5Id: Long? = null,
    val product6Id: Long? = null
)