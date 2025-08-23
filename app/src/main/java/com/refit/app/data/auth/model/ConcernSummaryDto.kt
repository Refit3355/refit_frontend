package com.refit.app.data.auth.model

data class ConcernSummaryDto(
    val health: HealthInfoDto? = null,
    val hair: HairInfoDto? = null,
    val skin: SkinInfoDto? = null
)