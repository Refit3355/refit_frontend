package com.refit.app.data.local.combination.repository

import com.refit.app.data.local.combination.api.CombinationApi
import com.refit.app.data.local.combination.model.LikedCombinationRequest
import com.refit.app.data.local.combination.model.CombinationsResponse
import com.refit.app.network.RetrofitInstance

class CombinationRepository(
    private val api: CombinationApi = RetrofitInstance.create(CombinationApi::class.java)
) {
    suspend fun getLikedCombinations(ids: List<Long>): Result<CombinationsResponse> = runCatching {
        api.getLikedCombinations(LikedCombinationRequest(ids))
    }
}
