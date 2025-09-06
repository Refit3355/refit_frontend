package com.refit.app.data.chatbot.model

data class ProductListRequestDto(
    val bhType: Int,            // 0: 피부/헤어, 1: 건강
    val effectIds: List<Int>,
    val sort: String = "latest",
    val lastId: Long? = null,
    val limit: Int = 20
)