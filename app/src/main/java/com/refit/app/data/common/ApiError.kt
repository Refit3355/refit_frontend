package com.refit.app.data.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String? = null,
    val message: String? = null
)