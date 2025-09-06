package com.refit.app.data.chatbot.usecase

import com.refit.app.data.product.model.Product
import com.refit.app.data.chatbot.repository.ChatbotRepository

class GetProductsUseCase(
    private val repository: ChatbotRepository
) {
    data class Params(
        val currentTemplateId: String, // "reco_skin_select" / "reco_hair_select" / "reco_health_select"
        val effectCode: Int,           // 칩 value
        val lastId: Long? = null,
        val limit: Int = 20,
        val sort: String = "latest"
    )

    suspend operator fun invoke(params: Params): Result<Output> {
        return try {
            val bhType = when (params.currentTemplateId) {
                "reco_skin_select", "reco_hair_select" -> 0
                "reco_health_select" -> 1
                else -> return Result.failure(IllegalArgumentException("Unsupported template: ${params.currentTemplateId}"))
            }

            val (items, totalCount) = repository.getProducts(
                bhType = bhType,
                effectIds = listOf(params.effectCode),
                sort = params.sort,
                lastId = params.lastId,
                limit = params.limit
            )
            Result.success(Output(items, totalCount))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    data class Output(
        val items: List<Product>,
        val totalCount: Int
    )
}
