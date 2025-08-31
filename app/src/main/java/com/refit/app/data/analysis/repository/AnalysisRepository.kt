package com.refit.app.data.analysis.repository

import android.content.Context
import android.net.Uri
import com.refit.app.data.analysis.api.AnalysisApi
import com.refit.app.data.analysis.model.FullAnalysisResponse
import com.refit.app.data.analysis.model.MultipartUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AnalysisRepository(private val api: AnalysisApi) {

    suspend fun analyzeFromUri(
        context: Context,
        imageUri: Uri,
        uiProductType: String
    ): FullAnalysisResponse = withContext(Dispatchers.IO) {
        val imagePart: MultipartBody.Part = MultipartUtils.uriToImagePart(context, imageUri)
        val typePart: RequestBody = MultipartUtils.textPart(
            MultipartUtils.mapProductTypeForBackend(uiProductType)
        )
        api.analyzeImage(imagePart, typePart)
    }

    suspend fun analyzeFromBytes(
        context: Context,
        imageBytes: ByteArray,
        uiProductType: String
    ): FullAnalysisResponse = withContext(Dispatchers.IO) {
        val imagePart = MultipartUtils.bytesToImagePart(context, imageBytes)
        val typePart = MultipartUtils.textPart(
            MultipartUtils.mapProductTypeForBackend(uiProductType)
        )
        api.analyzeImage(imagePart, typePart)
    }
}