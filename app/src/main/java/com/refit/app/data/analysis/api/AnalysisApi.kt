package com.refit.app.data.analysis.api

import com.refit.app.data.analysis.model.IngredientAnalysisResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalysisApi {
    @POST("/analysis/report")
    suspend fun postReport(@Body ingredients: List<String>): IngredientAnalysisResponse
}