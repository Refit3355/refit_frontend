package com.refit.app.data.combination.repository

import com.refit.app.data.combination.api.CombinationApi
import com.refit.app.data.combination.model.CombinationLikeResponse
import com.refit.app.data.combination.model.LikedCombinationRequest
import com.refit.app.data.combination.model.CombinationsResponse
import com.refit.app.network.RetrofitInstance

class CombinationRepository(
    private val api: CombinationApi = RetrofitInstance.create(CombinationApi::class.java)
) {
    suspend fun getLikedCombinations(ids: List<Long>): Result<CombinationsResponse> = runCatching {
        api.getLikedCombinations(LikedCombinationRequest(ids))
    }

    suspend fun likeCombination(combinationId: Long): Result<CombinationLikeResponse> = runCatching {
        api.likeCombination(combinationId)
    }

    suspend fun dislikeCombination(combinationId: Long): Result<CombinationLikeResponse> = runCatching {
        api.dislikeCombination(combinationId)
    }
}
