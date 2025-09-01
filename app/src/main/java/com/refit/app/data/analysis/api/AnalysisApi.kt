package com.refit.app.data.analysis.api

import com.refit.app.data.analysis.model.FullAnalysisResponse
import com.refit.app.data.analysis.model.IngredientAnalysisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AnalysisApi {

    @Multipart
    @POST("/analysis/image")
    @Headers("Requires-Auth: true")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part,
        @Part("productType") productType: RequestBody // "화장품" | "영양제"
    ): FullAnalysisResponse
}